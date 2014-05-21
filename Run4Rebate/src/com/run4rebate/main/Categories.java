package mk.run4rebate;

public class Categories {
	private long id;
	private String name;
	private String district_id;

	/*
	 * private String buildings;
	 */

	public Categories(String name) {
		this.name = name;
	}

	public Categories(long id, String name, String district_id)

	/*
	 * Clients(long id, String imageName, String gpsX, String gpsY, String
	 * sending)
	 */{
		this.id = id;
		this.name = name;
		this.district_id = district_id;
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

	public void setDistrictId(String district_id) {
		this.district_id = district_id;
	}

	/*
	 * public void setBuildings(String buildings){ this.buildings = buildings;}
	 */

	public String getName() {
		return name;
	}

	public String getDistrictId() {
		return district_id;
	}

	/*
	 * public String getBuildings(){return buildings;}
	 */

	public String toStringName() {
		return name;
	}

	public String toStringDistrictId() {
		return district_id;
	}
	/*
	 * public String toStringBuildings(){return buildings;}
	 */

}
