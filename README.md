[![Build Status](https://travis-ci.com/rtarar/fhirmessage.svg?token=NsExtwz1URrej53P1tZC&branch=master)](https://travis-ci.com/rtarar/fhirmessage)


# fhirmessage

This simple micronaut service performs two functions to support various aspects for FHIR Messaging and its testing during connectathons.

1. To convert any FHIR Bundle to a transaction bundle  "/transaction" , Body as FHIR XML or JSON Bundle Resource.
2. To process a FHIR transaction bundle and "unpack" the contents of the bundle to the FHIr Server.


