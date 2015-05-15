
import gnu.io.CommPortIdentifier;

import java.util.Enumeration;
import jssc2javaxcomm.JSSCCommPortIdentifier;

public class Main {
  
  private static void testListPort() {
    Enumeration e=gnu.io.CommPortIdentifier.getPortIdentifiers();
    while (e.hasMoreElements()) {
      Object o=e.nextElement();
      if (o instanceof JSSCCommPortIdentifier) {
	JSSCCommPortIdentifier i=(JSSCCommPortIdentifier)o;
	System.out.println(i.toString());
      }    else {
	System.out.println(o.toString());
      }  
    }
  }
  
  public static void main(String[] args) {
    testListPort();
    
  }
}
