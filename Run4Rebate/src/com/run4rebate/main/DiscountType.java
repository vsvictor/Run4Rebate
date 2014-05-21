package mk.run4rebate;

public class DiscountType {
	private long id;
	private String name;

	/*
	 * private String buildings;
	 */

	public DiscountType(String name) {
		this.name = name;
	}

	public DiscountType(long id, String name)

	/*
	 * Clients(long id, String imageName, String gpsX, String gpsY, String
	 * sending)
	 */{
		this.id = id;
		this.name = name;
		/*
		 * this.buildings = buildings;
		 */
	}

	public long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * public void setBuildings(String buildings){ this.buildings = buildings;}
	 */

	public String getName() {
		return name;
	}

	/*
	 * public String getBuildings(){return buildings;}
	 */

	public String toStringName() {
		return name;
	}
	/*
	 * public String toStringBuildings(){return buildings;}
	 */

}
