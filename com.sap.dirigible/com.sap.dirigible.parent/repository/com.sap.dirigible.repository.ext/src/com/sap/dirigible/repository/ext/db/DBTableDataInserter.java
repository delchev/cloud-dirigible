/*******************************************************************************
 * Copyright (c) 2014 SAP AG or an SAP affiliate company. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *******************************************************************************/

package com.sap.dirigible.repository.ext.db;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.sql.DataSource;

public class DBTableDataInserter {

	private static final String INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D = Messages.DBTableDataInserter_INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D;

	private static final String DELIMITER = "|"; //$NON-NLS-1$

	private static final String DOT = "."; //$NON-NLS-1$

	private static final String CLOSE = ")"; //$NON-NLS-1$

	private static final String VALUES = " VALUES ("; //$NON-NLS-1$

	private static final String INSERT_INTO = "INSERT INTO "; //$NON-NLS-1$

	private static final String Q = "?"; //$NON-NLS-1$

	private static final String COMMA = ","; //$NON-NLS-1$

	private static final int BATCH_SIZE = 500;

	private byte[] csvFileContent;
	private String tableName;
	private DataSource dataSource;

	public DBTableDataInserter(DataSource dataSource, byte[] csvFileContent, String fileName) {
		this.csvFileContent = csvFileContent;
		this.tableName = getFileNameWithoutExtension(fileName);
		this.dataSource = dataSource;
	}

	public void insert() throws Exception {
		Connection con = null;
		try {
			con = getConnection();

			List<String[]> records = readRecords(new ByteArrayInputStream(csvFileContent));
			
			insertRecords(con, records, tableName);
		} finally {
			closeConnection(con);
		}
	}

	private void insertRecords(Connection con, List<String[]> records,
			String tableName) throws SQLException {
		int columnsCount = records.get(0).length;
		PreparedStatement insertStat = con.prepareStatement(INSERT_INTO
				+ tableName + VALUES + generateQM(columnsCount) + CLOSE);

		int recordsInBatch = 0;

		for (String[] record : records) {
			for (int i = 0; i < record.length; i++) {
				insertStat.setString(i + 1, record[i]);
			}
			insertStat.addBatch();
			recordsInBatch++;

			if (recordsInBatch == BATCH_SIZE) {
				insertStat.executeBatch();
				recordsInBatch = 0;
			}
		}
		if (recordsInBatch != 0) {
			insertStat.executeBatch();
		}
	}

	private String generateQM(int number) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < number; i++) {
			result.append(Q);

			if (i + 1 < number) {
				result.append(COMMA);
			}
		}
		return result.toString();
	}

	private String[] getStringItems(String str) {
		String delimiter = DELIMITER;
		StringTokenizer tok = new StringTokenizer(str, delimiter, true);

		List<String> res = new ArrayList<String>();

		boolean delimiterIsPreviousToken = true;
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if (delimiter.equals(token)) {
				if (delimiterIsPreviousToken) {
					res.add((String) null);
				}
				delimiterIsPreviousToken = true;
			} else {
				res.add(token);
				delimiterIsPreviousToken = false;
			}
		}
		if (delimiterIsPreviousToken) {
			res.add((String) null);
		}

		String[] myArr = new String[res.size()];
		res.toArray(myArr);
		return myArr;
	}

	private List<String[]> readRecords(InputStream csvFile)
			throws FileNotFoundException, IOException,
			InvalidNumberOfElementsException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				csvFile));
		List<String[]> data = new ArrayList<String[]>();

		int item_count = -1;
		int line_number = 0;
		while (true) {
			String line = reader.readLine();
			line_number++;
			if (line == null) {
				break;
			}
			String[] items = getStringItems(line);
			if (item_count == -1) {
				item_count = items.length;
			} else if (item_count != items.length) {
				throw new InvalidNumberOfElementsException(
						String.format(
								INVALID_NUMBER_D_OF_ELEMENTS_AT_LINE_D_INITIAL_COLUMNS_NUMBER_D,
								items.length, line_number, item_count));
			}
			data.add(items);
		}
		reader.close();
		return data;
	}

	private String getFileNameWithoutExtension(String csvFileName) {
		return csvFileName.substring(0, csvFileName.lastIndexOf(DOT));
	}

	private void closeConnection(Connection con) throws SQLException {
		if (con != null) {
			con.close();
		}
	}

	private Connection getConnection() throws Exception {
		Connection con = dataSource.getConnection();
		return con;
	}

}
