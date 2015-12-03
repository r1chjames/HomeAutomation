package com.HomeAuto.dashboard.data;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.util.Arrays.asList;

/**
 * Created by sysdevan on 02/12/2015.
 */
public class MongoDB {

    public static void connect() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("HomeAuto");
        //boolean auth = db.authenticate('username', 'password');

        //DBCollection collection = db.getCollection("users");
        //DBObject firstDocument = collection.findOne(); System.out.println(firstDocument);


        //BasicDBObject document = new BasicDBObject( "name", "Fred").append("age", "28"); collection.insert(document);
    }
}
