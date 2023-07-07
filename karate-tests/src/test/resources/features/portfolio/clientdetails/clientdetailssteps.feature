@ignore
Feature: Create client Business
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl


  @ignore
  @createClientBusinessDetailsStep
  Scenario: Create client Business Details Step
    Given configure ssl = true
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(clientCreationDate)' }
    * def clientId = result.response.resourceId
    * def clientBusinessDetailsData = read('classpath:templates/clientBusinessDetails.json')
    Given path 'clients',clientId,'businessDetail'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientBusinessDetailsData.createClientBusinessDetails
    When method POST
    Then status 200
    Then match $ contains { resourceIdentifier: '#notnull' }
    Then match $ contains { clientId: '#notnull' }
    Then def clientId = response.clientId
    Then def businessId = response.resourceIdentifier
