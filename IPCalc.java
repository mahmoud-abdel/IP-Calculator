
public class IPCalc {
	
	private static int octet1;
	private static int octet2;
	private static int octet3;
	private static int octet4;
	private static String[] splitSecond = new String[4];
	
	private static String address = "168.24.17.5";
	private static String prefix = "/24";
	private static String binaryAddress = "";
	private static String ipClass = "";
	private static String subnetAddress = "";
	private static String broadcastAddress = "";
	private static int intPrefix;
	private static int dsm;
	private static int bb; //borrowed bits
	private static int hb; //host bits

	public static void main(String[] args) {
			
		address = args[0];
		prefix = args[1];
		intPrefix = Integer.parseInt(prefix.replace("/",""));
		splitSecond = address.split("\\.");
				
		octet1 = Integer.parseInt(splitSecond[0]);
		octet2 = Integer.parseInt(splitSecond[1]);
		octet3 = Integer.parseInt(splitSecond[2]);
		octet4 = Integer.parseInt(splitSecond[3]);
		binaryAddress = octetConvert(octet1) + octetConvert(octet2) + octetConvert(octet3) + octetConvert(octet4);
		ipClass = getIPClass();
		
		printAddress();
		printNetmask();
		printWildcard();
		System.out.println("=>");
		printSubnet();
		printBroadcast();
		printHostMin();
		printHostMax();
		printBorrowedBits();
		printSubnets();
		printSubnetIndex();
		printHostBits();
		printHostsPerNet();

	}
	
	public static String getIPClass() {
		String ipc = "";
		if (octet1 <= 127) {
			ipc = "(Class A)";
			dsm = 8;
		}
		else if (octet1 <= 191) {
			ipc = "(Class B)";
			dsm = 16;
		}
		else if (octet1 <= 223) {
			ipc = "(Class C)";
			dsm = 24;
		}
		else if (octet1 <= 239) {
			ipc = "(Class D)";
			dsm = 24;
		}
		else {
			ipc = "(Class E)";
			dsm = 24;
		}
		return ipc;
	}
	
	public static String octetConvert(int o) {
		String octet = Integer.toBinaryString(o);
		int length = octet.length();
		for (int i = 0; i < (8-length); i++) {
			octet = '0' + octet;
		}
		return octet;
	}
	
	public static boolean[] stringToBoolArr(String b) {
		boolean[] addy = new boolean[32];
		for (int i = 0; i < 32; i++) {
			if (b.charAt(i) == '1') {
				addy[i] = true;
			}
			else {
				addy[i] = false;
			}
		}
		return addy;
	}
	
	public static String boolArrToString(boolean[] ba) {
		String address = "";
		for (int i = 0; i < 32; i++) {
			if (ba[i]) {
				address += '1';
			}
			else {
				address += '0';
			}
		}
		return address;
	}
	
	public static String fancyBinary(String a) {
		String fancy = "";
		int counter = 8;
		for (int i = 0; i < 8; i++) {
			fancy += a.charAt(i);
		}
		for (int i = 0; i < 3; i++) {
			fancy += '.';
			for (int j = 0; j < 8; j++) {
				fancy += a.charAt(counter);
				counter++;
			}
		}
		return fancy;
	}
	
	public static String fancyDecimal (String a) {
		String fancy = "";
		int counter = 8;
		
		fancy += String.valueOf(Integer.parseInt(a.substring(0,8), 2));
		for (int i = 0; i < 3; i++) {
			String octet = a.substring(counter, counter+8);
			fancy += '.';
			fancy += String.valueOf(Integer.parseInt(octet, 2));
			counter += 8;
		}
		return fancy;
	}
	
	public static boolean[] getNetmask() {
		String mask = "";
		int hostBits = 32 - intPrefix;
		for (int i = 0; i < intPrefix; i++) {
			mask += '1';
		}
		for (int i = 0; i < hostBits; i++) {
			mask += '0';
		}
		boolean[] masky = stringToBoolArr(mask);
		return masky;
	}
	
	public static void printAddress() {
		String toPrint = "Address:    ";
		toPrint += address;
		toPrint += "           " + fancyBinary(binaryAddress);
		System.out.println(toPrint);
	}
	
	public static void printNetmask() {
		String toPrint = "Netmask:    ";
		toPrint += fancyDecimal(boolArrToString(getNetmask())) + " = " + intPrefix + "    ";
		toPrint += fancyBinary(boolArrToString(getNetmask()));
		System.out.println(toPrint);
	}
	
	public static void printWildcard() {
		boolean[] wildcard = getNetmask();
		String toPrint = "Wildcard:   ";
		for (int i = 0; i < 32; i++) {
			wildcard[i] = !wildcard[i];
		}
		toPrint += fancyDecimal(boolArrToString(wildcard)) + "           ";
		toPrint += fancyBinary(boolArrToString(wildcard));
		System.out.println(toPrint);
	}
	
	public static void printSubnet() {
		String toPrint = "Subnet (Network):";
		boolean[] subnet = new boolean[32];
		boolean[] mask = getNetmask();
		boolean[] address = stringToBoolArr(binaryAddress);
		for (int i = 0; i < 32; i++) {
			if (mask[i] && address[i]) {
				subnet[i] = true;
			}
			else {
				subnet[i] = false;
			}
		}
		subnetAddress = boolArrToString(subnet);
		toPrint += fancyDecimal(boolArrToString(subnet)) + prefix + "  ";
		toPrint += fancyBinary(boolArrToString(subnet)) + " " + ipClass;
		System.out.println(toPrint);
	}
	
	public static void printBroadcast() {
		String toPrint = "Broadcast:       ";
		boolean[] broadcast = stringToBoolArr(binaryAddress);
		for (int i = intPrefix; i < 32; i++) {
			broadcast[i] = true;
		}
		broadcastAddress = boolArrToString(broadcast);
		toPrint += fancyDecimal(boolArrToString(broadcast)) + "   ";
		toPrint += fancyBinary(boolArrToString(broadcast));
		System.out.println(toPrint);
		
	}
	
	public static void printHostMin() {
		boolean[] hostMin = stringToBoolArr(subnetAddress);
		hostMin[31] = true;
		String toPrint = "HostMin (FHIP):  ";
		toPrint += fancyDecimal(boolArrToString(hostMin)) + "     ";
		toPrint += fancyBinary(boolArrToString(hostMin));
		System.out.println(toPrint);
	}
	
	public static void printHostMax() {
		boolean[] hostMax = stringToBoolArr(broadcastAddress);
		hostMax[31] = false;
		String toPrint = "HostMax (LHIP):  ";
		toPrint += fancyDecimal(boolArrToString(hostMax)) + "   ";
		toPrint += fancyBinary(boolArrToString(hostMax));
		System.out.println(toPrint);
	}
	
	public static void printBorrowedBits() {
		String toPrint = "s=";
		bb = intPrefix - dsm;
		toPrint += bb;
		System.out.println(toPrint);
	}
	
	public static void printSubnets() {
		String toPrint = "S=";
		toPrint += (int)Math.pow(2, bb);
		System.out.println(toPrint);
	}
	
	public static void printSubnetIndex() {
		String index = "";
		int intIndex;
		String toPrint = "Subnet index (";
		for (int i = dsm; i < intPrefix; i++) {
			index += binaryAddress.charAt(i);
		}
		intIndex = Integer.parseInt(index, 2);
		toPrint += index + ") = " + intIndex;
		System.out.println(toPrint);
	}
	
	public static void printHostBits() {
		String toPrint = "h=";
		hb = 32 - intPrefix;
		toPrint += hb;
		System.out.println(toPrint);
	}
	
	public static void printHostsPerNet() {
		String toPrint = "HIPs Hosts/Net: ";
		int hpn = ((int)Math.pow(2, hb)) - 2;
		toPrint += hpn;
		System.out.println(toPrint);
	}

}


