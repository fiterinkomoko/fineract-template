@ignore
Feature: Create loan stapes
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def productsData = read('classpath:templates/savings.json')

  @ignore
  @reviewLoanApplicationStage
  Scenario: Review Application Stage
    Given configure ssl = true
    * def loansData = read('classpath:templates/loansDecision.json')
    Given path 'loans/decision/reviewApplication',loanId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.reviewApplication
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }