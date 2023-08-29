# String Repository

This web application is intended to serve as a tool for managing translatable strings used in applications which are used globally across different languages.  In using these translatable strings within an application a string code, along with an ISO language and country codes is used to determine the translated string to be presented to the user in their language.  Typically, these strings will have placeholders for values that are supplied at runtime, such as "Hello John", where "John" is a value provided at runtime. To accomodate this, the value to be replaced at runtime is specified as "%#", where "%1" represents the first replaceable value, "%2" the second and so on.  Below is a screenshot showing various translations of a HELLO text into multiple languages:

![image](https://github.com/Brian-Wintz/StringRepository/assets/133924124/d39df995-22a0-4728-ba99-f6740baa415a)


In implementing this solution I adopted the minimal set of components to create this web application as specified below:

* Maria DB: database for storing the translated string records (encoded as UTF-8)
* Apache Tomcat: serves as both the web server for providing static content, as well as a servlet container for exposing REST APIs using a servlet
* GSON: used to map the TranslatedString java object from it's JSON representation
* JQuery: used to make the REST API calls using ajax, as well as dynamically manipulating the HTML
* Java: implementation language for the backend
* JavaScript: implementation language for the web frontend

In developing this tool there were some interesting coding challenges addressed:
* Using a generic mechanism that provides for create/read/update/delete (crud) management of a database record for a POJO
* Identify what information needs to be configured by the POJO to allow this generic crud management to work
* Managing multi-byte unicode text between web front end and backend
* Efficiently managing a pool of database connections to allow for multiple concurrent users
* Managing HTML table content using JQuery
* Implementing REST APIs using a Java servlet
* Implementing calls from web application using JQuery
* Mapping data between Java objects and JSON

For this example, a simple TranslatedString POJO is used that contains the following fields:

  String StringCode - unique value used to identify which text is to be translated
  String LanguageCode - ISO language code used to specify which language the text is translated for
  String CountryCode - ISO country code to further refine the language to a specific country.  For example, Spanish is different in Spain (ES) than it is in Mexico (MX).
  String RegionCode - Not generally used, but there could be cases where a translation could be further refined by a country's region, such as Texas (tx) within the US.
  String Text - the translated text for the specified StringCode, LanguageCode, CountryCode and RegionCode
  String Ticket - a ticket under which the original (English) translated string was created
  
Although this implementation is sufficient for managing these translated strings, there is opportunity for additional development to improve this tool:

* Need to implement error handling
* Implement data validations, such as ensuring ISO language and country codes are valid and verifying that the ticket is available and in a proper state for creating or updating the record
* Incorporate use of drop down boxes for specifying ISO language and country codes
* Add more filtering, such as only displaying records for a specific ISO language
