package com.ll;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class Employee {
  private String id;
  private String name;
  private int age;
  private double salary;

  public Employee(String name, int age, double salary) {
    this.name = name;
    this.age = age;
    this.salary = salary;
  }

  public Document toDocument() {
    return new Document()
        .append("name", name)
        .append("age", age)
        .append("salary", salary);
  }
}

public class DbOperationsTest {
  private static MongoClient mongoClient;
  private static MongoDatabase database;
  private static MongoCollection<Document> collection;

  @BeforeEach
  void setUp() {
    mongoClient = MongoClients.create("mongodb://localhost:27017");
    database = mongoClient.getDatabase("testdb");
    collection = database.getCollection("employees");
  }

  @AfterEach
  void tearDown() {
    collection.drop();
    mongoClient.close();
  }

  @Test
  void testCreateEmployee() {
    Employee employee = new Employee("João Silva", 30, 5000.0);
    collection.insertOne(employee.toDocument());

    Document found = collection.find(new Document("name", "João Silva")).first();
    assertNotNull(found);
    assertEquals(30, found.getInteger("age"));
  }

  @Test
  void testReadEmployee() {
    Employee employee = new Employee("Maria Santos", 25, 4500.0);
    collection.insertOne(employee.toDocument());

    Document found = collection.find(new Document("name", "Maria Santos")).first();
    assertNotNull(found);
    assertEquals(4500.0, found.getDouble("salary"));
  }

  @Test
  void testUpdateEmployee() {
    Employee employee = new Employee("Carlos Lima", 35, 6000.0);
    collection.insertOne(employee.toDocument());

    collection.updateOne(
        new Document("name", "Carlos Lima"),
        new Document("$set", new Document("salary", 6500.0)));

    Document found = collection.find(new Document("name", "Carlos Lima")).first();
    assertEquals(6500.0, found.getDouble("salary"));
  }

  @Test
  void testDeleteEmployee() {
    Employee employee = new Employee("Ana Souza", 28, 4800.0);
    collection.insertOne(employee.toDocument());

    collection.deleteOne(new Document("name", "Ana Souza"));

    Document found = collection.find(new Document("name", "Ana Souza")).first();
    assertNull(found);
  }
}
