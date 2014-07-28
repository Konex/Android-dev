ormlite usage example
===========
**Set up**
Follow the instructions [here](http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android)

Note:
Whenever you modify your db classes, don't forget to run the OrmLiteConfigUtil as well!
The ormlite_config.txt will be auto-generated under yourApp/res/raw/

In the **DbHelper.java**, we use a HashMap to hold all the DAOs. 

	private HashMap maps;
	
	public Object getRuntimeDao(Class<?> type) {
		if (maps == null) maps = new HashMap<String, Object>();
		
		if (!maps.containsKey(type.getSimpleName())) {
			Object obj = getRuntimeExceptionDao(type);
			maps.put(type.getSimpleName(), obj);
		}
		
		return maps.get(type.getSimpleName());
	}

Then you can get access to a DAO by:
	RuntimeExceptionDao<yini, Integer> yiniDao = 
	(RuntimeExceptionDao<yini, Integer>)mDbHelper.getRuntimeDao(yini.class);
				QueryBuilder<yini, Integer> qb = yiniDao.queryBuilder();
				qb.where().eq(yini.IS_COOL, true);
				bookablePods = yiniDao.query(qb.prepare());

