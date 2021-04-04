package io.metersphere.jmeter.reporters;

import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadGroupAutoStop
        extends AbstractListenerElement
        implements SampleListener, Serializable,
        TestStateListener, Remoteable, NoThreadClone {

    private static final Logger log = LoggerFactory.getLogger(ThreadGroupAutoStop.class);
    private final static String DELAY_SECONDS = "delay_seconds";
    private long curSec = 0L;
    private long startTime = 0;
    private int stopTries = 0;
    private int delaySeconds = 0;
    private AtomicBoolean once = new AtomicBoolean(false);
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public ThreadGroupAutoStop() {
        super();
    }

    @Override
    public void sampleOccurred(SampleEvent se) {
        AbstractThreadGroup threadGroup = JMeterContextService.getContext().getThreadGroup();
        if (threadGroup instanceof ThreadGroup) {
            if (!((ThreadGroup) threadGroup).getScheduler()) {
                return;
            }
            long duration = ((ThreadGroup) threadGroup).getDuration();
            long offset = System.currentTimeMillis() / 1000 - (startTime + duration);
            if (offset >= 0) {
                if (!once.getAndSet(true)) {
                    executorService.schedule(() -> {
                        threadGroup.tellThreadsToStop();
                        log.info("Expected duration reached, shutdown the ThreadGroup");
                    }, delaySeconds, TimeUnit.SECONDS);
                }
            }
        }
        if (threadGroup instanceof ConcurrencyThreadGroup) {
            long holdSeconds = ((ConcurrencyThreadGroup) threadGroup).getHoldSeconds();
            long rampUpSeconds = ((ConcurrencyThreadGroup) threadGroup).getRampUpSeconds();
            long offset = System.currentTimeMillis() / 1000 - (startTime + holdSeconds + rampUpSeconds);
            if (offset >= 0) {
                if (!once.getAndSet(true)) {
                    executorService.schedule(() -> {
                        threadGroup.tellThreadsToStop();
                        log.info("Expected duration reached, shutdown the ConcurrencyThreadGroup");
                    }, delaySeconds, TimeUnit.SECONDS);
                }
            }
        }
    }

    @Override
    public void sampleStarted(SampleEvent se) {
        System.out.println("starting....");
    }

    @Override
    public void sampleStopped(SampleEvent se) {
        System.out.println("end....");
    }

    @Override
    public void testStarted() {
        curSec = 0;
        stopTries = 0;
        startTime = System.currentTimeMillis() / 1000;
        //init test values
        delaySeconds = getDelaySecsAsInt();
    }

    @Override
    public void testStarted(String string) {
        testStarted();
    }

    @Override
    public void testEnded() {
    }

    @Override
    public void testEnded(String string) {
    }


    void setDelaySecs(String text) {
        setProperty(DELAY_SECONDS, text);
    }

    String getDelaySecs() {
        return getPropertyAsString(DELAY_SECONDS);
    }


    private int getDelaySecsAsInt() {
        int res = 0;
        try {
            res = Integer.valueOf(getDelaySecs());
        } catch (NumberFormatException e) {
            log.error("Wrong delay seconds: " + getDelaySecs(), e);
            setDelaySecs("30");
        }
        return res > 0 ? res : 30;
    }


    private void stopTest() {
        stopTries++;

        if (JMeter.isNonGUI()) {
            log.info("Stopping JMeter via UDP call");
            stopTestViaUDP("StopTestNow");
        } else {
            if (stopTries > 10) {
                log.info("Tries more than 10, stop it NOW!");
                StandardJMeterEngine.stopEngineNow();
            } else if (stopTries > 5) {
                log.info("Tries more than 5, stop it!");
                StandardJMeterEngine.stopEngine();
            } else {
                JMeterContextService.getContext().getEngine().askThreadsToStop();
            }
        }
    }

    private void stopTestViaUDP(String command) {
        try {
            int port = JMeterUtils.getPropDefault("jmeterengine.nongui.port", JMeter.UDP_PORT_DEFAULT);
            log.info("Sending " + command + " request to port " + port);
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = command.getBytes("ASCII");
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        }

    }
}
