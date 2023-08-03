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



  @ignore
  @dueDiligenceStage
  Scenario: Due Diligence Stage
    Given configure ssl = true
    * def loansData = read('classpath:templates/loansDecision.json')
    Given path 'loans/decision/dueDiligence',loanId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.dueDiligence
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }

  @ignore
  @collateralReviewStage
  Scenario: Collateral Review Stage
    Given configure ssl = true
    * def loansData = read('classpath:templates/loansDecision.json')
    Given path 'loans/decision/collateralReview',loanId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.collateralReview
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }

  @ignore
  @createLoanApprovalMatrixStep
  Scenario: Create Loan Approval Matrix Step
    Given configure ssl = true
    * def matrix = read('classpath:templates/loanApprovalMatrix.json')
    Given path 'loans/decision/approvalMatrix/createApprovalMatrix'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request matrix.loanApprovalMatrix
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def matrixId = response.resourceId


  @ignore
  @deleteLoanApprovalMatrixStep
  Scenario: Delete Loan Approval Matrix Step
    Given configure ssl = true
    Given path 'loans/decision/approvalMatrix/',matrixId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method DELETE
    Then status 200
    Then match $ contains { resourceId: '#notnull' }

  @ignore
  @createLoanApprovalMatrixAndShouldFailDueToDuplicateCurrencyStep
  Scenario: Create Loan Approval Matrix and should fail due to duplicate Currency Step
    Given configure ssl = true
    * def matrix = read('classpath:templates/loanApprovalMatrix.json')
    Given path 'loans/decision/approvalMatrix/createApprovalMatrix'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request matrix.loanApprovalMatrix
    When method POST
    Then status 403
    Then match $ contains { developerMessage: '#notnull' }