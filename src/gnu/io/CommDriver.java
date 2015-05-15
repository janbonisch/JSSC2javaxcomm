package gnu.io;

public interface CommDriver {

  public abstract CommPort getCommPort(String portName, int portType);

  public abstract void initialize();
}
