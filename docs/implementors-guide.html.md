# Implementors Guide

## `/generate-request`
+ To begin a Verify journey, an AuthnRequest is needed - see [here](http://alphagov.github.io/rp-onboarding-tech-docs/pages/saml/samlWorks.html)
+ To generate an AuthnRequest, use the <code>/generate-request</code> endpoint with the format specified [here](https://alphagov.github.io/verify-service-provider/#requestgenerationbody)
+ Endpoint accepts a level of assurance which has previously been discussed with Verify

+ <code>/generate-request</code> returns either a `200 OK` response containing an object specified (here) or an error reponse in the format specified (here).

### `200 OK`
+ Store the <code>requestId</code> in the users session as it will be needed later to translate the response
+ `requestId` storage must be secure to prevent responses being replayed by a man-in-the-middle attack
+ <code>samlRequest</code> contains a base64 encoded SAML AuthnRequest
+ AuthnRequest should be submitted to the relevant SSO Location (compliance tool/integration/production) as an HTML form as per (saml spec link)
+ Should escape values before inserting into HTML
+ Should make form auto-post to improve user experience
+ Should style page if JS disabled

### `422 UNPROCESSABLE ENTITY`
+ Returned if request contained invalid JSON
+ Unrecognised field if JSON tags wrong
+ Unrecognised value if LoA not in [LEVEL_1, LEVEL_2]
+ Should show user an appropriate error page

### `500 INTERNAL SERVER ERROR`
+ Logs error in VSP logs and returns log line ID
+ Should show user an appropriate error page

Example
____
```
<form method='post' action='{ssoLocation}'>
    <h1>Send SAML Authn request to hub</h1>
    <input type='hidden' name='SAMLRequest' value='{samlRequest}'/>
    <input type='hidden' name='relayState' value=''/>
    <button>Submit</button>
</form>
<!-- Automatically post the HTML form containing the AuthnRequest -->
<script>
    var form = document.forms[0]
    form.setAttribute('style', 'display: none;')
    window.setTimeout(function () { form.removeAttribute('style') }, 5000)
    form.submit()
</script>
```

## `/translate-response`
+ Hub will send a base64 encoded SAML Response to your service 'assertion consumer URL'
+ To translate a SAML Response, use the `/translate-response` endpoint
+ Requires the base64 encoded SAML Response, request ID from the user's session and minimum level of assurance.
+ [JSON schema](https://alphagov.github.io/verify-service-provider/#requestgenerationbody)

### `200 OK`
+ Response was successfully translated
+ Scenario will be one of list documented [here](https://alphagov.github.io/verify-service-provider/#translatedresponsebody-properties-enumerated-values)
+ Not all `200 OK` responses mean the user authenticated
+ `200 OK` means that SAML was valid and could be translated

#### Scenario
##### SUCCESS_MATCH
+ User was matched in local matching service
+ Use `pid` from response to get user from local database
+ Attributes will be empty

##### ACCOUNT_CREATION
+ User could not be matched
+ Should only be returned if user account creation enabled in MSA
+ Save user in local database
+ Should store `pid` for future matching
+ Attributes contain data to populate database row

##### NO_MATCH
+ User could not be matched
+ Should only be returned if user account creation disabled in MSA
+ Show 'could not match you' page to user (clear with Sanjay)
+ Attributes will be empty

##### CANCELLATION
+ User cancelled Verify journey
+ Inform user and optionally offer alternative routes (talk to Sanjay)

##### AUTHENTICATION_FAILED
+ User could not be authenticated
+ 'Something went wrong page' (talk to Sanjay)

#### REQUEST_ERROR
+ User could not be authenticated
+ 'Something went wrong page' (talk to Sanjay)

### `400 BAD_REQUEST`
+ Something about SAML was wrong
+ Either wrong format or invalid (eg. wrong signature)
+ Contains `ErrorMessage` body described [here](https://alphagov.github.io/verify-service-provider/#errormessage) detailing error
+ Should show user an appropriate error page

### `422 UNPROCESSABLE ENTITY`
+ Returned if request contained invalid JSON
+ Contains `ErrorMessage` body described [here](https://alphagov.github.io/verify-service-provider/#errormessage) detailing error
+ Unrecognised field if JSON tags wrong
+ Unrecognised value if LoA not in [LEVEL_1, LEVEL_2] or other values not strings
+ Should show user an appropriate error page

### `500 INTERNAL SERVER ERROR`
+ Logs error in VSP logs and returns log line ID
+ Contains `ErrorMessage` body described [here](https://alphagov.github.io/verify-service-provider/#errormessage) detailing error
+ `/translate-response` returns a `200 OK` response if the response was successfully translated
+ Should show user an appropriate error page

