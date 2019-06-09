# JU Library
## Introuction

**JU Library** is a mobile phone application, for attendance management in the JECRC University's library, to keep the data in a digitalised format, rather than in a physical register. 

In this I will provide a QR code, which will be scanned on the app, that'll reside in student's mobile phone.

## Why we need this system? 

To keep track of who has come in the library, at what time, University has to maintain a big register. Now, the problems with physical registers are :

**1. Secure Data:** The data is written, by student on a page in the register. Any physical harm like liquid spill, intentionally changing the data by a human element, or even keeping a registry in a place where pages get eaten by rodents or fungus, makes it risky to keep such valuable data in a physical register.

**2. Correct Data:** There may be chances that a student might not write correct or full information about him/her and slips away, since it is practically not possible to check properly for each student, especially when there are a lot of students at a time waiting in line to mark the entry in register.

**3. Easy Analysis:** The data is written by a human, managed by a human, and suppose if university wants to keep track of how many students came in last day/month/year, or of all the years since the university has started, even just to do general analysis, it is comparatively easier to do it with technology.

4. Supposedly, a student goes missing or meets with an unfortunate event, or something goes wrong in the library itself then, university library data present in the database, can help in finding at which time the particular student, was present and what time he left etc. 

5. Since QR code scanning technology, in an android application,  which is connected to the database at the back-end, is used, it is easier to do analytics and surveys regarding improving the library. 

6. Any regular nuisance creating student can be easily blacklisted from entering the library, which is nearly handled loosely when human elements are involved.

## Blue print flow diagram

![alt text](https://github.com/Girrajjangid/LibraryProject/blob/master/Untitled-Project%20(1).jpg)

## Application flow diagram

![alt text](https://github.com/Girrajjangid/LibraryProject/blob/master/Untitled-Project.jpg)

## What technologies used?

**1 MongoDB Stitch**: It used to analysis and visualize the entries.
**2 Firebase** : It used to store the Student's data, Push Notification and OTP services.

