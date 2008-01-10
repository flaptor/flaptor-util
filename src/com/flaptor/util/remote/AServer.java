package com.flaptor.util.remote;

import java.util.Map;

import org.apache.log4j.Logger;

import com.flaptor.util.AStoppableThread;
import com.flaptor.util.Execute;
import com.flaptor.util.RunningState;
import com.flaptor.util.Stoppable;

/**
 * This class imprements several convenience methods to implement a generic server.
 * A generic server is a transport (rpc) wrapper class that takes one or many objects (the
 * handlers) and exports their functionality through rpc.
 * @see com.flaptor.util.Stoppable
 */
public abstract class AServer implements Stoppable {

	private static final Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());

	private final boolean keepRunning;
	private KeepAliveThread keepAliveThread = null;
	protected final int port;
	protected boolean stopRequested = false;
	protected boolean started = false;

	/**
	 * Default constructor.
	 * Equivalent to AServer(port, false).
	 * @param port the port to bind the server. Must be > 0. Ports <= 1024 will log a warning.
	 * @throws IllegalArgumentException if the port is an invalid number.
	 */
	protected AServer(final int port) {
		this(port, false);
	}

	/**
	 * Constructor.
	 * @param port the port to bind the server. Must be > 0. Ports <= 1024 will log a warning.
	 * @param keepRunning tells the server if it has to start a new thread to keep the server running.
	 * @throws IllegalArgumentException if the port is an invalid number.
	 */
	protected AServer(final int port, boolean keepRunning) {
		if (port <=0 || port > 65536) {
			throw new IllegalArgumentException("port must be > 0 and < 65536");
		}
		if (port <= 1024) {
			logger.warn("starting a server in a well known port");
		}
		this.port = port;
		this.keepRunning = keepRunning;
	}

	/**
	 * Starts the server.
	 */
	synchronized public final void start() {
		startServer();
		if (keepRunning) {
			keepAliveThread = new KeepAliveThread();
			new Thread(keepAliveThread).start();
		}
		started = true;
	}

	synchronized public void requestStop() {
		stopRequested = true;
		requestStopServer();
		for (Map.Entry<String, ? extends Object> entry : getHandlers().entrySet()) {
			Object handler = entry.getValue();
			if (!(handler instanceof Stoppable)) continue;
			Stoppable toStop = (Stoppable) handler;
			if (!toStop.isStopped()) ((Stoppable)handler).requestStop();
		}
		if (null != keepAliveThread) {
			keepAliveThread.requestStop();
		}
	}

	public boolean isStopped() {
		if (!isStoppedServer()) return false;
		for (Map.Entry<String, ? extends Object> entry : getHandlers().entrySet()) {
			Object handler = entry.getValue();
			if (!(handler instanceof Stoppable)) continue;
			if (!((Stoppable)handler).isStopped()) return false;
		}
		return true;
	}

	/**
	 * 
	 * @return true iff the RPC server is stopped 
	 */
	protected abstract boolean isStoppedServer();
	
	/**
	 * request the stopping of the RPC server 
	 */
	protected abstract void requestStopServer();
		
	/**
	 * Starts the actual server.
	 */
	protected abstract void startServer();

	/**
	 * Returns the handler created by the derived server.
	 */
	protected abstract Map<String, ? extends Object> getHandlers();


	/**
	 * A simple class used to retain a reference of the server in case the
	 * main thread quits.
	 */
	private class KeepAliveThread extends AStoppableThread { 
		public void run() {
			while (!signaledToStop) {
				if (AServer.this.isStopped()) {
					requestStop();
				}
				sleep(1000);
			}
			stopped = true;
		}
		private boolean canStop() {
			for (Map.Entry<String, ? extends Object> entry : getHandlers().entrySet()) {
				Object handler = entry.getValue();
				if (!(handler instanceof Stoppable)) return false;
				if (!((Stoppable)handler).isStopped()) return false;
			}
			return true;
		}
	}
}
