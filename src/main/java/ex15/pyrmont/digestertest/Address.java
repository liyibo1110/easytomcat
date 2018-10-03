package ex15.pyrmont.digestertest;

public class Address {

	private String streetName;
	private String streetNumber;
	
	public Address() {
		System.out.println("....Creating Address");
	}
	
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		System.out.println("....Setting streetName : " + streetName);
		this.streetName = streetName;
	}
	public String getStreetNumber() {
		System.out.println("....Setting streetNumber : " + streetNumber);
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	@Override
	public String toString() {
		return "...." + streetNumber + " " + streetName;
	}
	
}
