Feature:  Create Client Business Details APIs
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @createClientBusinessDetails
  Scenario: Create Client Business Details

        #- Disable configuration  ---Enable-Client-Business-Detail---
    *  def configName = 'Enable-Client-Business-Detail'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(response.globalConfig.id)' }

       #-Get code and code values for SourceOfCapital
    *  def sourceOfCapitalCode = 'SourceOfCapital'
    *  def sourceOfCapitalCodeResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(sourceOfCapitalCode)' }
    *  def sourceOfCapitalCodeId = sourceOfCapitalCodeResponse.codeName.id

       #-Get code and code values for businessType
    *  def businessTypeCode = 'BusinessType'
    *  def businessTypeResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(businessTypeCode)' }
    *  def businessTypeCodeId = businessTypeResponse.codeName.id

        #- Fetch codeValue for SourceOfCapital
    * def codeValueResSC = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(sourceOfCapitalCodeId)' }
    * def SourceOfCapitalCodeValueId = codeValueResSC.listOfCodeValues[0].id
    * print SourceOfCapitalCodeValueId

      #- Fetch codeValue for businessType
    * def codeValueResBT = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(businessTypeCodeId)' }
    * def businessTypeCodeValueId = codeValueResBT.listOfCodeValues[0].id
    * print businessTypeCodeValueId


    * def submittedOnDate = df.format(faker.date().past(370, 369, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

    * def clientDetailsResponse = call read('classpath:features/portfolio/clientdetails/clientdetailssteps.feature@createClientBusinessDetailsStep'){clientCreationDate : '#(submittedOnDate)' ,businessType : '#(businessTypeCodeValueId)' ,sourceOfCapital : '#(SourceOfCapitalCodeValueId)' ,clientId : '#(clientId)' }
    * def businessDetailId = clientDetailsResponse.businessId
    #- Get Client business Details by clientI and businessIdd
    * def clientDetail = call read('classpath:features/portfolio/clientdetails/clientdetailssteps.feature@getClientBusinessDetailsStep'){clientId : '#(clientId)' ,businessDetailId : '#(businessDetailId)' }
    * print clientDetail

        #- Get Templates for Client  business Details by Id
    * def templateResponse = call read('classpath:features/portfolio/clientdetails/clientdetailssteps.feature@getTemplatesClientDetails'){ clientId : '#(clientId)' }
    * print templateResponse
