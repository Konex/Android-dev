Passing Data Between Activities and Services
===========
##Primitive Data Types
To share primitive data between Activities/Services in an application, use Intent.putExtras(). For passing primitive data that needs to persist use the Preferences storage mechanism.

##Non-Persistent Objects
For sharing complex non-persistent user-defined objects for short duration, the following approaches are recommended:

**Singleton class**
You can take advantage of the fact that your application components run in the same process through the use of a singleton. This is a class that is designed to have only one instance. It has a static method with a name such as getInstance() that returns the instance; the first time this method is called, it creates the global instance. Because all callers get the same instance, they can use this as a point of interaction. For example activity A may retrieve the instance and call setValue(3); later activity B may retrieve the instance and call getValue() to retrieve the last set value.

	public class Singleton
	{
		private static Singleton instance;

		public static String customVar="Hello";

		public static void initInstance()
		{
			if (instance == null)
			{
			  // Create the instance
			  instance = new Singleton();
			}
		}

		public static synchronized Singleton getInstance()
		{
			 if (instance == null)
			 {
				// Create the instance
				instance = new Singleton();
			 }
			 return instance;
		}

		private Singleton()
		{
			// Constructor hidden because this is a singleton
		}
	
		public void customSingletonMethod()
		{
		 // Custom method
		}
	}
	
So you can use it anyway in your app like this:

	Singleton singleton = Singleton.getInstance();
	singleton.customSingletonMethod();

**A public static field/method**
An alternate way to make data accessible across Activities/Services is to use public static fields and/or methods. You can access these static fields from any other class in your application. To share an object, the activity which creates your object sets a static field to point to this object and any other activity that wants to use this object just accesses this static field.

**A HashMap of WeakReferences to Objects**
You can also use a HashMap of WeakReferences to Objects with Long keys. When an activity wants to pass an object to another activity, it simply puts the object in the map and sends the key (which is a unique Long based on a counter or time stamp) to the recipient activity via intent extras. The recipient activity retrieves the object using this key.	

##Passing and sharing data

**Global variable**

	/**
	 * This class is to hold anything that to be shared across all activities.
	 * For instance: this class can be also used to store 
	 * auth token retrieved from a web server.
	 */    
	public class YiniGlobal extends Application {
		private int userId = -1;
		private String loggedInUserName = null;
		private String encryptedPassword = null;
		
		public String getLoggedInUserName() {
			return loggedInUserName;
		}

		public void setLoggedInUserName(String loggedInUserName) {
			this.loggedInUserName = loggedInUserName;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getEncryptedPassword() {
			return encryptedPassword;
		}

		public void setEncryptedPassword(String encryptedPassword) {
			this.encryptedPassword = encryptedPassword;
		}
		
		public void reset() {
			this.userId = -1;
			this.loggedInUserName = null;
			this.encryptedPassword = null;
		}
	}
	
**Bundle**	
	
**Parcelable**	

    public class PersonParcelable implements Parcelable {
		private String name;
		private String surname;
		private String email;
		// Get and Set methods
 
		@Override
		public int describeContents() {
		    return hashCode();
		}
 
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(name);
			dest.writeString(surname);
			dest.writeString(email);
		}
 
		// We reconstruct the object reading from the Parcel data
		public PersonParcelable(Parcel p) {
			name = p.readString();
			surname = p.readString();
			email = p.readString();
		}
 
		public PersonParcelable() {}
 
		// We need to add a Creator
		public static final Parcelable.Creator<PersonParcelable> CREATOR = 
			new Parcelable.Creator<PersonParcelable>() {
	 
			@Override
			public PersonParcelable createFromParcel(Parcel parcel) {  
				return new PersonParcelable(parcel);
			}
		 
			@Override
			public PersonParcelable[] newArray(int size) {  
				return new PersonParcelable[size];
			}
		};
	}	

In activity A:

	Intent i = new Intent(EditActivity.this, ViewActivity.class);
	PersonParcelable p = new PersonParcelable();
	p.setName(edtName.getText().toString());
	p.setSurname(edtSurname.getText().toString());
	p.setEmail(edtEmail.getText().toString());
	i.putExtra("myPersonParcelable", p);
	startActivity(i);

In activity B:

	Bundle b = i.getExtras();
	Person p = (Person) b.getParcelable("myPersonParcelable");
	String name = p.getName();
	String surname = p.getSurname();
	String email = p.getEmail();

**Generic parcelable**

	public class MyParcel implements Parcelable {
		//A hashmap to hold your objects
		private HashMap<String,Object> parcels;

		public MyParcel(){
			//INITIALIZING A HASHMAP
			parcels = new HashMap<String,Object>();
		}

		public void readFromParcel(Parcel in) {
			int count = in.readInt();
			for (int i = 0; i < count; i++) {
				parcels.put(in.readString(), in.readValue(Object.class.getClassLoader()));
			}
		}

		public MyParcel(Parcel in) {
			parcels = new HashMap<String,Object>();
			readFromParcel(in);
		}

		@Override
		public int describeContents() {
			return parcels != null ? parcels.size() : 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(parcels.size());
			for (String s: (Set<String>)parcels.keySet()) {
				dest.writeString(s);
				dest.writeValue(parcels.get(s));
			}
		}
		
		public static final Parcelable.Creator<MyParcel> CREATOR=new Parcelable.Creator<MyParcel>() {

			@Override
			public MyParcel createFromParcel(Parcel source) {
				return new MyParcel(source);
			}

			@Override
			public MyParcel[] newArray(int size) {
				return new MyParcel[size];
			}
		};

		public Object get(String key){
			return parcels.get(key);
		}

		public void put(String key, Object value) {
			parcels.put(key, value);
		}
	}

In activity A:

	MyParcel myParcel = new MyParcel();
	myParcel.put("MyObject", new Date());//sends a blank date object.
	Intent contentIntent = new Intent(ActivityA.this, ActivityB.class);
	contentIntent.putExtra("My Parcel Key", myParcel);
	startActivity(contentIntent);

In activity B:

	Bundle contetnBundle = getIntent().getExtras();
	MyParcel myParcel = contetnBundle.getParcelable("My Parcel Key");
	Date myDateWithActivityA = (Date)myParcel.get("MyObject");
