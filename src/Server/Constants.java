package Server;

public class Constants {
	public static final int port=4000;
	public static final String USER_AGENT="Shafa";
	public static final String INSERT_FORMAT_SQL="INSERT INTO items (name,address"
										+ ",owner_id,category,size,price,description,lat,lng)"
										+ "VALUES (`%1$s`,`%2$s`,`%3$d`,`%4$s`,"
										+ "`%5$s`,`%6$f`,`%7$s`,`%8$f`,`%9$f`);";
	public static final String INSERT_FORMAT_POST="name=%1$s&address=%2$s&owner_id=%3$d"
								+ "&category=%4$s&size=%5$s&price=%6$f&description=%7$s&"
								+ "lat=%8$f&lng=%9$f";

/*
INSERT INTO table_name (column1,column2,column3,...)
	VALUES (value1,value2,value3,...);


 CREATE TABLE `items` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `name` VARCHAR( 60 ) NOT NULL ,
  `address` VARCHAR( 80 ) NOT NULL ,
  `owner_id` INT NOT NULL ,
  `category` VARCHAR( 255 ) NOT NULL ,
  `size` VARCHAR( 80 ) NOT NULL ,
  `price` FLOAT( 10,3) NOT NULL ,
  `description` TEXT NOT NULL ,
  `lat` FLOAT( 10, 6 ) NOT NULL ,
  `lng` FLOAT( 10, 6 ) NOT NULL
) ENGINE = InnoDB ;
*/
}


