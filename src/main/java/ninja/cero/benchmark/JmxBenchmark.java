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

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@State(Scope.Benchmark)
public class JmxBenchmark {

    public static final String PID = "12345";
    static VirtualMachine vm;
    static JMXConnector connector;
    static ThreadMXBean threadMXBean;
    static long sum;

    @Benchmark
    public void no1_vmAttach() throws IOException, AttachNotSupportedException {
        VirtualMachine vm = VirtualMachine.attach(PID);
        vm.getSystemProperties();
        vm.detach();
    }

    @Benchmark
    public void no2_vmCachedGetProperties() throws IOException, AttachNotSupportedException {
        vm.getSystemProperties();
    }

    @Benchmark
    public void no3_vmCachedGetConnector() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        JMXConnector jmxConnector = getJmxConnector();
        jmxConnector.close();
    }

    @Benchmark
    public void no4_connectorCachedGetMBeanCount() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        connection.getMBeanCount();
    }

    @Benchmark
    public void no5_connectorCachedThreadCount() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        Object count = connection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "ThreadCount");
        sum += (Integer) count;
    }

    @Benchmark
    public void no6_connectorCachedGetThreadCount() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ThreadMXBean threadBean = ManagementFactory.newPlatformMXBeanProxy(
                connection, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
        sum += threadBean.getThreadCount();
    }

    @Benchmark
    public void no7_beanCachedGetThreadCount() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        sum += threadMXBean.getThreadCount();
    }

    @Setup
    public void setUp() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        System.out.println("setUp");
        vm = VirtualMachine.attach(PID);
        connector = getJmxConnector();
        threadMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
    }

    @TearDown
    public void tearDown() throws IOException {
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
