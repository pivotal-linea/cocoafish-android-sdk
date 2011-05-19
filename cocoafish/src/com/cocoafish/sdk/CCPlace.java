package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class CCPlace extends CCObject implements Parcelable {
	private String name;
	private String address;
	private String address2;
	private String crossStreet;
	private String city;
	private String region;
	private String country;
	private double longitude;
	private double latitude;
	private String phone;
	
    public static final Parcelable.Creator<CCPlace> CREATOR = 
        new Parcelable.Creator<CCPlace>() { 
        public CCPlace createFromParcel(Parcel in) { 
        	return new CCPlace(in);
    
        } 
        public CCPlace[] newArray(int size) { 
            return new CCPlace[size]; 
        } 
    }; 
    
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public String getCrossStreet() {
		return crossStreet;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getCountry() {
		return country;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getFullAddress() {
		String fullAddress = "";
		if (address != null && address.length() > 0) {
			fullAddress = address;
		}
		if (address2 != null && address2.length() > 0) {
			fullAddress += ", " + address2;
		}
		if (city != null && city.length() > 0) {
			fullAddress += ", " + city;
		}
		if (region != null && region.length() > 0) {
			fullAddress += ", " + region;
		}
		return fullAddress;
	}
	
	public CCPlace(JSONObject jObject) throws CocoafishError {
		super(jObject);
			try {
				name = jObject.getString("name").trim();
			} catch (JSONException e1) {
				throw new CocoafishError("Invalid Server Response: CCPlace: Missing name");
			}
		try {
			address = jObject.getString("address").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: CCPlace: Missing address1");
		}
		try {
			city = jObject.getString("city").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: CCPlace: Missing city");
		}
		try {
			address2 = jObject.getString("address2").trim();
		} catch (Exception e){
		}
		try {
			crossStreet = jObject.getString("crossStreet").trim();
		} catch (Exception e){
		}
		try {
			region = jObject.getString("region").trim();
		} catch (Exception e){
		}
		try {
			phone = jObject.getString("phone").trim();
		} catch (Exception e){
		}
		try {
			country = jObject.getString("country").trim();
		} catch (Exception e){
		}
		try {
			latitude = Double.valueOf(jObject.getString("latitude").trim()).doubleValue();
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPlace: lat is not double");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPlace: Missing lat");
		}
		try {
			longitude = Double.valueOf(jObject.getString("longitude").trim()).doubleValue();
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPlace: lng is not double");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPlace: Missing lng");
		}
	}
	
	private CCPlace(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int flag) {
    	out.writeString(objectId);
        out.writeString(name);
        out.writeString(address);
        out.writeString(address2);
        out.writeString(crossStreet);
        out.writeString(city);
        out.writeString(region);
        out.writeString(country);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }

    public void readFromParcel(Parcel in) {
    	objectId = in.readString();
        name = in.readString();
        address = in.readString();
        address2 = in.readString();
        crossStreet = in.readString();
        city = in.readString();
        region = in.readString();
        country = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

	@Override
	public int describeContents() {
		return 0;
	}

}


