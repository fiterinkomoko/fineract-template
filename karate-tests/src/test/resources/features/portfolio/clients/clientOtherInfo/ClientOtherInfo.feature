Feature: Test client other info apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @ignore
  @createClientOtherInfo
  Scenario: Create client other info

    #- Fetch codeValue for strata
    * def strataCodeId = 10160
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(strataCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : strataCodeId, name : 'Test'});
    * def strataCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print strataCodeValueId

    #- Fetch codeValue for nationality
    * def nationalityCodeId = 28
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(nationalityCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : nationalityCodeId, name : 'Test'});
    * def nationalityCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print nationalityCodeValueId

    #- Fetch codeValue for year arrived in country
    * def yearArrivedCodeId = 10161
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(yearArrivedCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : yearArrivedCodeId, name : '1980'});
    * def yearArrivedCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print yearArrivedCodeValueId

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Create client without address
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)'}
    * def createdClientId = result.clientId

    #- Create client other info
    * def createdClientOtherInfo = call read('classpath:features/portfolio/clients/clientOtherInfo/ClientOtherInfoSteps.feature@create') { clientId : '#(createdClientId)', strataId : '#(strataCodeValueId)', nationalityId : '#(nationalityCodeValueId)', yearArrivedInHostCountryId : '#(yearArrivedCodeValueId)'}
    * def createdClientOtherInfoId = createdClientOtherInfo.client
    * print createdClientOtherInfoId


  @updateClientOtherInfo
  Scenario: Update client other info

    #- Enable configuration  ---Enable-other-client-info
    *  def enableOtherInfoConfigName = 'Enable-other-client-info'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(enableOtherInfoConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Fetch codeValue for strata
    * def strataCodeId = 10160
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(strataCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : strataCodeId, name : 'Test1'});
    * def strataCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print strataCodeValueId

    #- Fetch codeValue for nationality
    * def nationalityCodeId = 28
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(nationalityCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : nationalityCodeId, name : 'Test1'});
    * def nationalityCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print nationalityCodeValueId

    #- Fetch codeValue for year arrived in country
    * def yearArrivedCodeId = 10161
    * def codeValueRes = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(yearArrivedCodeId)'}
    * def res = if(karate.sizeOf(codeValueRes.listOfCodeValues) < 1) karate.call('classpath:features/system/codes/codeValuesStep.feature@createCodeValueStep', { codeId : yearArrivedCodeId, name : '1990'});
    * def yearArrivedCodeValueId = (res != null ? res.codeValueId : codeValueRes.listOfCodeValues[0].id)
    * print yearArrivedCodeValueId

    #- Disable configuration  ---address
    *  def addressConfigName = 'Enable-Address'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(addressConfigName)' }
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(response.globalConfig.id)' }

    #- Create client without address
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)'}
    * def createdClientId = result.clientId

    #- Create client other info
    * def createdClientOtherInfo = call read('classpath:features/portfolio/clients/clientOtherInfo/ClientOtherInfoSteps.feature@create') { clientId : '#(createdClientId)', strataId : '#(strataCodeValueId)', nationalityId : '#(nationalityCodeValueId)', yearArrivedInHostCountryId : '#(yearArrivedCodeValueId)'}
    * def createdClientOtherInfoId = createdClientOtherInfo.otherInfoId
    * print createdClientOtherInfoId

    # Update client other info
    * def updatedClientOtherInfo = call read('classpath:features/portfolio/clients/clientOtherInfo/ClientOtherInfoSteps.feature@update') { clientId : '#(createdClientId)', otherInfoId : '#(createdClientOtherInfoId)', strataId : '#(strataCodeValueId)', nationalityId : '#(nationalityCodeValueId)', yearArrivedInHostCountryId : '#(yearArrivedCodeValueId)'}
    * assert infoId == updatedClientOtherInfo.res.resourceId
    * match updatedClient.res.changes contains { numberOfChildren: '#notnull'}
    * assert 2 == updatedClient.res.changes.numberOfChildren
