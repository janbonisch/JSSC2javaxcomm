package gnu.io;

import java.io.FileDescriptor;
import java.util.Enumeration;
import jssc2javaxcomm.JSSCCommPortIdentifier;

public class CommPortIdentifier {

  private static final String EXCEPTION_MESSAGE="Not supported.";
  public static final int PORT_SERIAL=1;
  public static final int PORT_PARALLEL=2;
  public static final int PORT_I2C=3;
  public static final int PORT_RS485=4;
  public static final int PORT_RAW=5;

  public void addPortOwnershipListener(CommPortOwnershipListener c) {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public void removePortOwnershipListener(CommPortOwnershipListener c) {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public String getCurrentOwner() {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public String getName() {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public int getPortType() {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public boolean isCurrentlyOwned() {
    throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
  }

  public CommPort open(FileDescriptor f) throws UnsupportedCommOperationException {
    throw new UnsupportedCommOperationException();
  }

  public CommPort open(String TheOwner, int i) throws gnu.io.PortInUseException {
    throw new PortInUseException();
  }

  //----------------------------------------------------------------------------
  public static void addPortName(String s, int type, CommDriver c) {
    JSSCCommPortIdentifier.addPortName(s, type, c);
  }

  static public CommPortIdentifier getPortIdentifier(CommPort p) throws NoSuchPortException {
    return JSSCCommPortIdentifier.getPortIdentifier(p);
  }

  static public CommPortIdentifier getPortIdentifier(String s) throws NoSuchPortException {
    return JSSCCommPortIdentifier.getPortIdentifier(s);
  }

  static public Enumeration getPortIdentifiers() {
    return JSSCCommPortIdentifier.getPortIdentifiers();
  }
}
