package jssc2javaxcomm;

import gnu.io.CommDriver;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class JSSCCommPortIdentifier extends CommPortIdentifier {

  private String name;
  private String owner;

  private void init(String name) {
    this.name=name;
    this.owner=System.getProperty("os.name");
  }

  private JSSCCommPortIdentifier() {
    init("unknown");
  }

  private JSSCCommPortIdentifier(String name) {
    init(name);
  }
  
  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getCurrentOwner() {
    return owner;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getPortType() {
    return CommPortIdentifier.PORT_SERIAL;
  }

  @Override
  public boolean isCurrentlyOwned() {
    return false;
  }

  @Override
  public CommPort open(String TheOwner, int i) throws PortInUseException {
    return new JSSCSerialPort(name);
  }

  //----------------------------------------------------------------------------
  static public Enumeration<CommPortIdentifier> getPortIdentifiers() {
    ArrayList<CommPortIdentifier> r=new ArrayList();
    for (String s : jssc.SerialPortList.getPortNames()) {
      r.add(new JSSCCommPortIdentifier(s));
    }
    return Collections.enumeration(r); //a vracime vysledek jako enum
  }

  static public CommPortIdentifier getPortIdentifier(String s) throws NoSuchPortException {
    for (String l : jssc.SerialPortList.getPortNames()) {
      if (s.equalsIgnoreCase(l)) {
	return new JSSCCommPortIdentifier(l);
      }
    }
    throw new NoSuchPortException();
  }

  static public CommPortIdentifier getPortIdentifier(CommPort p) throws NoSuchPortException {
    return getPortIdentifier(p.getName());
  }

  public static void addPortName(String s, int type, CommDriver c) {
  }
}
