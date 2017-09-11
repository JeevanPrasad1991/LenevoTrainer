package cpm.com.lenovotraining.geocode;

public class GeotaggingBeans {
	
	
	
	public String storeid;

	public String getGEO_TAG() {
		return GEO_TAG;
	}

	public void setGEO_TAG(String GEO_TAG) {
		this.GEO_TAG = GEO_TAG;
	}

	public String GEO_TAG="N";

	public String url1;
	public String url2;
	public String url3;
	public double Latitude ;
	public double Longitude;
	
	
	public int imageflag1;
	
	public int imageflag2;
	
	public int imageflag3;

	
	
	public String getStoreid() {
		return storeid;
	}
	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}
	public int getImageflag1() {
		return imageflag1;
	}
	public void setImageflag1(int imageflag1) {
		this.imageflag1 = imageflag1;
	}
	public int getImageflag2() {
		return imageflag2;
	}
	public void setImageflag2(int imageflag2) {
		this.imageflag2 = imageflag2;
	}
	public int getImageflag3() {
		return imageflag3;
	}
	public void setImageflag3(int imageflag3) {
		this.imageflag3 = imageflag3;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double d) {
		Latitude = d;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double d) {
		Longitude = d;
	}
	
	public void setStoreId(String storeid)
	{
		
		this.storeid=storeid;
	}
	
	public String getStoreId()
	{
		
		return storeid;
	}
	
	public void setUrl1(String url1)
	{
		
		this.url1=url1;
	}
	
	public String getUrl1()
	{
		
		return url1;
	}
	
	public void setUrl2(String url2)
	{
		
		this.url2=url2;
	}
	
	public String getUrl2()
	{
		
		return url2;
	}
	
	public void setUrl3(String url3)
	{
		
		this.url3=url3;
	}
	
	public String getUrl3()
	{
		
		return url3;
	}

}
