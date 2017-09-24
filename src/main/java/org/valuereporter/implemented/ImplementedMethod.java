package org.valuereporter.implemented;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ImplementedMethod {
    private String serviceName = "";
    private final String name;


    public ImplementedMethod(String name) {
        this.name = name;
        serviceName = "not-set";
    }
    public ImplementedMethod(String serviceName, String name) {
        this.serviceName = serviceName;
        this.name = name;
    }



    public String getName() {
        return name;
    }

    public String getPrefix() {
        return serviceName;
    }


    @Override
    public String toString() {
        return "ObservedMethod{" +
                "name='" + name +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

    public String toCsv() {
        return new String(getPrefix() +"," + getName());

    }

    public void setPrefix(String serviceName) {
        this.serviceName = serviceName;
    }
}
