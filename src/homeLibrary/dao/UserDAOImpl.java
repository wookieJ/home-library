package homeLibrary.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import homeLibrary.model.User;
import homeLibrary.util.ConnectionProvider;

public class UserDAOImpl implements UserDAO
{
	private final static String CREATE_USER = "INSERT INTO user(username, email, password) VALUES(:username, :email, :password);";
	private final static String SET_USER_ROLE = "INSERT INTO user_role(username) VALUES(:username);";
	private final static String READ_USER = "SELECT user_id, username, email, password FROM user WHERE user_id= :user_id;";
	private final static String READ_USER_BY_USERNAME = "SELECT user_id, username, email, password FROM user WHERE username = :username";
	private final static String READ_USER_ROLE = "SELECT role_name FROM user_role WHERE username = :username;";

	private NamedParameterJdbcTemplate template;

	public UserDAOImpl()
	{
		template = new NamedParameterJdbcTemplate(ConnectionProvider.getDataSource());
	}

	@Override
	public User create(User user)
	{
		User copyUser = new User(user);
		KeyHolder holder = new GeneratedKeyHolder();

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(user);
		System.out.println("Tworzenie user'a...");
		int update = template.update(CREATE_USER, paramSource, holder);
		if (update > 0)
		{
			copyUser.setId((Long) holder.getKey());
			SqlParameterSource param = new BeanPropertySqlParameterSource(copyUser);
			template.update(SET_USER_ROLE, param);
		}

		return copyUser;
	}

	@Override
	public User read(Long primaryKey)
	{
		User user = null;
		SqlParameterSource paramSource = new MapSqlParameterSource("user_id", primaryKey);
		user = template.queryForObject(READ_USER, paramSource, new UserRowMapper());

		return user;
	}

	private class UserRowMapper implements RowMapper<User>
	{
		@Override
		public User mapRow(ResultSet resultSet, int rowNum) throws SQLException
		{
			User user = new User();
			user.setId(resultSet.getLong("user_id"));
			user.setUsername(resultSet.getString("username"));
			user.setEmail(resultSet.getString("email"));
			user.setPassword(resultSet.getString("password"));
			return user;
		}
	}

	@Override
	public boolean update(User user)
	{
		return false;
	}

	@Override
	public boolean delete(Long primaryKey)
	{
		return false;
	}

	@Override
	public List<User> getAll()
	{
		return null;
	}

	@Override
	public User getUserByUsername(String username)
	{
		User resultUser = null;
		SqlParameterSource paramSource = new MapSqlParameterSource("username", username);
		resultUser = template.queryForObject(READ_USER_BY_USERNAME, paramSource, new UserRowMapper());

		return resultUser;
	}

	@Override
	public String getUserRoleByUsername(String username)
	{
		String role = new String();
		SqlParameterSource paramSource = new MapSqlParameterSource("username", username);
		role = template.queryForObject(READ_USER_ROLE, paramSource, new RowMapper<String>()
		{
			@Override
			public String mapRow(ResultSet resultSet, int row) throws SQLException
			{
				return resultSet.getString("role_name");
			}

		});

		return role;
	}
}
