package diary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DiaryEntity {
	public abstract String toString();
	public abstract String detailedString(DBConn conn) throws SQLException;
}
