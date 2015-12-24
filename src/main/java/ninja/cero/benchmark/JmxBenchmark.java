package ninja.cero.benchmark;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@State(Scope.Benchmark)
public class JmxBenchmark {

    public static final String PID = "15484";
    static VirtualMachine vm;
    static JMXConnector connector;
    static ThreadMXBean threadMXBean;
    static long sum;

    @Benchmark
    public void no1_vmAttach() throws Exception {
        VirtualMachine vm = VirtualMachine.attach(PID);
        vm.getSystemProperties();
        vm.detach();
    }

    @Benchmark
    public void no2_vmCachedGetProperties() throws Exception {
        vm.getSystemProperties();
    }

    @Benchmark
    public void no3_vmCachedGetConnector() throws Exception {
        JMXConnector jmxConnector = getJmxConnector();
        jmxConnector.close();
    }

    @Benchmark
    public void no4_connectorCachedGetMBeanCount() throws Exception {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        connection.getMBeanCount();
    }

    @Benchmark
    public void no5_connectorCachedThreadCount() throws Exception {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        Object count = connection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "ThreadCount");
        sum += (Integer) count;
    }

    @Benchmark
    public void no6_connectorCachedGetThreadCount() throws Exception {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ThreadMXBean threadBean = ManagementFactory.newPlatformMXBeanProxy(
                connection, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
        sum += threadBean.getThreadCount();
    }

    @Benchmark
    public void no7_beanCachedGetThreadCount() throws Exception {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        sum += threadMXBean.getThreadCount();
    }

    @Setup
    public void setUp() throws Exception {
        System.out.println("setUp");
        vm = VirtualMachine.attach(PID);
        connector = getJmxConnector();
        threadMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
    }

    @TearDown
    public void tearDown() throws Exception {
        System.out.println("tearDown");
        if (connector != null) {
            connector.close();
            connector = null;
        }

        if (vm != null) {
            vm.detach();
            vm = null;
        }
    }

    protected JMXConnector getJmxConnector() throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
        String connectorAddress = vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");

        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);
            connectorAddress = vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
        }

        JMXServiceURL serviceURL = new JMXServiceURL(connectorAddress);
        return JMXConnectorFactory.connect(serviceURL);
    }
}
