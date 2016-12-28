# Super Cool Image Sharing Site - A cyber security project #
This software was made for the F-Secure cyber security base MOOC.
As it is intentionally written to be vulnerable, you are running it at your own risk.
There also might be other vulnerabilities that are not listed in this document.

## How to run ##

### Option 1 ###
1. Clone or download the repository 
2. Open in NetBeans
3. Press "Run Project"

### Option 2 ###
1. Download SuperCoolIMageSharingSite-1.0.jar
2. Run SuperCoolIMageSharingSite-1.0.jar

## Vulnerabilities ##
All the instructions assume that you have the server running on your own computer in port 8080. The server has to user accounts premade. 
Username: tiivi, password: taavi and username: tele, password: tappi.

### A2 Broken Authentication and Session Management ###
Session IDs are predictable as they're ascending integers starting from 0. E.g. third user has JSESSIONID = 2 and fourth user JSESSIONID = 3.
Also session timeouts are set incorrectly, so if you don't press log out you will remain logged in for a year even if you close your browser.

1. Browse to http://127.0.0.1:8080
2. Click "My account" to log with previously made account or register a new account by clicking "Register" and then log in
3. Choose a random image to be uploaded.
4. Press "Add!"
5. Close your browser without logging out from the site and wait X minutes/seconds
6. Open your browser and browse to http://127.0.0.1:8080/user
7. You should still be logged in (If you have changed your browser settings to remove all cookies on exit this won't work)


### A3 Cross-Site Scripting (XSS) ###
The description field is vulnerable to JavaScript as it parses HTML tags. You can take advantage of it with following examples or come up with your own.

1. Browse to http://127.0.0.1:8080
2. Click "My account" to log with previously made account or register a new account by clicking "Register" and then log in
3. Open a private browsing window and login to the site with different account.
3. Choose a random image to be uploaded and copy-paste one of the following scripts to description field using the account in the private browsing window.
4. Press "Add!"
5. Choose the first account logged in and browse to the "Index" page, you should see a text that says "You have been logged out."

Force user to log out:
```html
<script type="text/javascript"> window.location.href = "http://127.0.0.1:8080/logout";</script>
```
Get sessionID with JavaScript:
```html
<script type="text/javascript">alert(document.cookie);</script>
```

### A5 Security Misconfiguration ###
Admin console is still enable for database access and it won't require admin privileges.

1. http://127.0.0.1:8080/h2-console/
2. Log in with previously made account
3. Put "jdbc:h2:mem:testdb" without quotes to JDBC URL field
4. Press connect
5. Click "ACCOUNT" on the left side of the page and then click run
6. You should now see a table with all registered usernames and passwords


### A7 Missing Function Level Access Control ###
The image deletion method is vulnerable as the server doesn't check if user has rights to delete that particular image.

1. Browse to http://127.0.0.1:8080
2. Click "My account" to log with previously made account or register a new account by clickin "Register" and then log in
3. Add two or three random images with descriptions.
4. Browse to "Index"
5. Right click one of the images and choose copy image url.
6. Open up a private browsing window, paste the copied url and add "/delete" to the url
7. Hit enter, the page redirects to login page. Browse to index and you should see that the one image is deleted.

Just add /delete to image url like shown below. ID is the number image that will be deleted.
http://127.0.0.1:8080/image/ID/delete


### A8 Cross-Site Request Forgery (CSRF) ### 
The password change method is vulnerable to CSRF as it uses GET. You can inject the code below to description field and every user that is logged in
and browses to "Index" password will be changed to NEWPASSWORD. As it is a CSRF attack, this works from some other site also. 
(If you're really mean you can change their passwords and then force them to log out.)
```html
<img src="http://127.0.0.1:8080/changepw?newpassword=NEWPASSWORD" alt="PWNED"/>
```
1. Browse to http://127.0.0.1:8080
2. Click "My account" to log with previously made account or register a new account by clicking "Register" and then log in
3. Choose a random image and add the following line to description field:
```html
<img src="http://127.0.0.1:8080/changepw?newpassword=NEWPASSWORD" alt="PWNED"/>
```
4. Press "Add!"
5. Open a private browsing window and login in to the site with different account.
6. Browse to index and log out
7. If you try to login with the original password you should get "Invalid username and password." 



## How to fix the vulnerabilities ##

### A2 Broken Authentication and Session Management ###
Remove lines 41-44 from DefaultController.java lines 
Remove http.sessionManagement().sessionFixation().none(); from SecuritConfiguration.java from SecurityConfiguration.java line 30.
Remove file application.properties
Remove lines 44-54 from user.html

### A3 Cross-Site Scripting (XSS) ###
Remove all http.header() related stuff from SecurityConfiguration.java lines 24-28.
Replace th:utext with th:text in index.html line 20.

### A5 Security Misconfiguration ###
Add "http.authorizeRequests().antMatchers("/h2-console/").denyAll();" to line 33 in SecurityConfiguration.java.

### A7 Missing Function Level Access Control  ###
Remove .antMatchers("/images/*/delete").permitAll() from SecurityConfiguration.java line 41.
Remove lines 99-104 in ImageController.java
Change th:method to DELETE in user.html line 28
Remove "/delete" from th:href in user.html line 28


### A8 Cross-Site Request Forgery (CSRF) ### 
Remove HTML comment tags ```html(<!-- and -->)``` from user.html line 40.
In file ImageController.java move lines 112 and 113 inside the curly bracket on lines 109 and 111


