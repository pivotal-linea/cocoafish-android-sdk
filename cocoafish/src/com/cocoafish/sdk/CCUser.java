package com.cocoafish.sdk;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.json.JSONObject;

public class CCUser extends CCObject implements Externalizable {
	private String first;
	private String last;
	private String email;
	private String userName;
	
    private static final int FFIRST = 0x01;
    private static final int FLAST = 0x02;
    private static final int FEMAIL = 0x04;
    private static final int FUSERNAME = 0x08;

    private transient int nullMask = 0;
    
	public String getFirst() {
		return first;
	}
	
	public String getLast() {
		return last;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUserName() {
		return userName;
	}

    public CCUser() {
        super();
    }
    
	public CCUser(JSONObject jObject) throws CocoafishError {
		super(jObject);

		try {
			first = jObject.getString("first").trim();
		} catch (Exception e) {
		}
		
		try {
			last = jObject.getString("last").trim();
		} catch (Exception e) {
		}
		
		try {
			email = jObject.getString("email").trim();
		} catch (Exception e) {
		}
		
		try {
			userName = jObject.getString("userName").trim();
		} catch (Exception e) {			
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
        nullMask = in.readInt();

        if ((nullMask & FFIRST) == 0) {
            this.first = in.readUTF();
        }

        if ((nullMask & FLAST) == 0) {
            this.last = in.readUTF();
        }

        if ((nullMask & FEMAIL) == 0) {
            this.email = in.readUTF();
        }

        if ((nullMask & FUSERNAME) == 0) {
            this.userName = in.readUTF();
        }

 	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		nullMask |= (first == null) ? FFIRST : 0;
        nullMask |= (last == null) ? FLAST : 0;
        nullMask |= (email == null) ? FEMAIL : 0;
        nullMask |= (userName == null) ? FUSERNAME : 0;
        out.writeInt(nullMask);

        if ((nullMask & FFIRST) == 0) {
            out.writeUTF(first);
        }

        if ((nullMask & FLAST) == 0) {
            out.writeUTF(last);
        }

        if ((nullMask & FEMAIL) == 0) {
            out.writeUTF(email);
        }

        if ((nullMask & FUSERNAME) == 0) {
            out.writeUTF(userName);
        }
	}


}


