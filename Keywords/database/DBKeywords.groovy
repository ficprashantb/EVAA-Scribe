package database



import com.kms.katalon.core.annotation.Keyword

public class DBKeywords {
	@Keyword
	def insertOrUpdate(String query, List params = []) {
		return MySqlHelper.executeUpdate(query, params)
	}

	@Keyword
	def selectOne(String query, List params = []) {
		return MySqlHelper.selectOne(query, params)
	}

	@Keyword
	def selectAll(String query, List params = []) {
		return MySqlHelper.selectAll(query, params)
	}

	@Keyword
	def transaction(Closure work) {
		MySqlHelper.withTransaction(work)
	}
}
