package com.bretth.osmosis.mysql.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.bretth.osmosis.OsmosisRuntimeException;
import com.bretth.osmosis.data.Way;


/**
 * Reads all ways from a database ordered by their identifier.
 * 
 * @author Brett Henderson
 */
public class WayReader extends EntityReader<Way> {
	private static final String SELECT_SQL =
		"SELECT id, timestamp FROM current_ways ORDER BY id";
	
	
	/**
	 * Creates a new instance.
	 * 
	 * @param host
	 *            The server hosting the database.
	 * @param database
	 *            The database instance.
	 * @param user
	 *            The user name for authentication.
	 * @param password
	 *            The password for authentication.
	 */
	public WayReader(String host, String database, String user, String password) {
		super(host, database, user, password);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ResultSet createResultSet(DatabaseContext queryDbCtx) {
		return queryDbCtx.executeStreamingQuery(SELECT_SQL);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Way createNextValue(ResultSet resultSet) {
		long id;
		Date timestamp;
		
		try {
			id = resultSet.getLong("id");
			timestamp = resultSet.getTimestamp("timestamp");
		} catch (SQLException e) {
			throw new OsmosisRuntimeException("Unable to read way fields.", e);
		}
		
		return new Way(id, timestamp);
	}
}