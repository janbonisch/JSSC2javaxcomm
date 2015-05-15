package jssc2javaxcomm;

import gnu.io.CommDriver;
import gnu.io.CommPort;

public class JSSCDriver implements CommDriver {

  @Override
  public CommPort getCommPort(String portName, int portType) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void initialize() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
