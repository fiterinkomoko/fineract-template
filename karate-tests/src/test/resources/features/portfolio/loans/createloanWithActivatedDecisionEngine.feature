Feature: Test loan account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @testThatICanCreateLoanAccountAndShouldNotApproveItIfDecisionEngineIsActivated
  Scenario: Test That I Can Create Loan Account And should not Approve it if Decision Engine is Activated
            #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    *  def configName = 'Add-More-Stages-To-A-Loan-Life-Cycle'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configurationId = response.globalConfig.id
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(configurationId)' }

    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #approval should fail
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveLoanShouldFailWhenDecisionEngineIsActivatedAndWorkFlowIsViolated') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

    #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    Then print 'Configuration ID ==> ', configurationId
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(configurationId)' }
    Then print 'Configuration Response ==> ', configResponse


  @testThatICanCreateLoanAccountAndShouldNotDisburseItIfDecisionEngineIsActivated
  Scenario: Test That I Can Create Loan Account And should not Approve it if Decision Engine is Activated


    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #approval should fail
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

       #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    *  def configName = 'Add-More-Stages-To-A-Loan-Life-Cycle'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configurationId = response.globalConfig.id
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(configurationId)' }

    #- Disbursement should here
    * def disbursementDate = submittedOnDate
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseLoanShouldFailWhenDecisionEngineIsActivatedAndWorkFlowIsViolated') { loanAmount : '#(loanAmount)', disbursementDate : '#(disbursementDate)', loanId : '#(loanId)' }

    #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    Then print 'Configuration ID ==> ', configurationId
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(configurationId)' }
    Then print 'Configuration Response ==> ', configResponse

  @testThatICanCreateLoanAccountAndReviewApplicationStageInDecisionEngine
  Scenario: Test That I Can Create Loan Account And Review Application Stage In Decision Engine
           #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    *  def configName = 'Add-More-Stages-To-A-Loan-Life-Cycle'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configurationId = response.globalConfig.id
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(configurationId)' }


    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #Review Loan Application Stage With Decision Stage
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@reviewLoanApplicationStage') { loanReviewOnDate : '#(submittedOnDate)', loanId : '#(loanId)' }

    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    #     Assert that Loan Account has passed REVIEW_APPLICATION Stage
    * assert loanResponse.loanAccount.loanDecisionState.id == 1000
    * assert loanResponse.loanAccount.loanDecisionState.value == 'REVIEW_APPLICATION'
    * assert loanResponse.loanAccount.isExtendLoanLifeCycleConfig == true

    #- Disable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    Then print 'Configuration ID ==> ', configurationId
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(configurationId)' }
    Then print 'Configuration Response ==> ', configResponse



  @testThatICanCreateLoanAccountAndReviewApplicationTo_Due_Diligence_StageInDecisionEngine
  Scenario: Test That I Can Create Loan Account And Review Application Stage In Decision Engine
           #- Enable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    *  def configName = 'Add-More-Stages-To-A-Loan-Life-Cycle'
    *  def response = call read('classpath:features/portfolio/configuration/configurationsteps.feature@findByNameStep') { configName : '#(configName)' }
    *  def configurationId = response.globalConfig.id
    *  def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@enable_global_config') { configurationsId : '#(configurationId)' }


    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #Review Loan Application Stage With Decision Stage
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@reviewLoanApplicationStage') { loanReviewOnDate : '#(submittedOnDate)', loanId : '#(loanId)' }

    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
    #     Assert that Loan Account has passed REVIEW_APPLICATION Stage
    * assert loanResponse.loanAccount.loanDecisionState.id == 1000
    * assert loanResponse.loanAccount.loanDecisionState.value == 'REVIEW_APPLICATION'
    * assert loanResponse.loanAccount.isExtendLoanLifeCycleConfig == true

     #-Get code and code values for SurveyLocation
    *  def surveyLocationCode = 'SurveyLocation'
    *  def surveyLocationResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(surveyLocationCode)' }
    *  def surveyLocationCodeId = surveyLocationResponse.codeName.id
    #-----------------------------------------------------------
          #- Fetch codeValue for SurveyLocation
    * def codeValueResSL = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(surveyLocationCodeId)' }
    * def surveyLocationValueId = codeValueResSL.listOfCodeValues[0].id

         #-Get code and code values for COUNTRY
    *  def countryCode = 'COUNTRY'
    *  def countryResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(countryCode)' }
    *  def countryCodeId = countryResponse.codeName.id
    #-----------------------------------------------------------
          #- Fetch codeValue for country
    * def codeValueResC = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(countryCodeId)' }
    * def countryValueId = codeValueResC.listOfCodeValues[0].id


             #-Get code and code values for Program
    *  def programCode = 'Program'
    *  def programResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(programCode)' }
    *  def programCodeId = programResponse.codeName.id
    #-----------------------------------------------------------
          #- Fetch codeValue for Program
    * def codeValueResP = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(programCodeId)' }
    * def programValueId = codeValueResP.listOfCodeValues[0].id

                 #-Get code and code values for Cohort
    *  def cohortCode = 'Cohort'
    *  def cohortResponse = call read('classpath:features/system/codes/codesStep.feature@fetchCodeByNameStep') { codeName : '#(cohortCode)' }
    *  def cohortCodeId = cohortResponse.codeName.id
    #-----------------------------------------------------------
          #- Fetch codeValue for Cohort
    * def codeValueResCT = call read('classpath:features/system/codes/codeValuesStep.feature@fetchCodeValuesStep'){ codeId : '#(cohortCodeId)' }
    * def cohortValueId = codeValueResCT.listOfCodeValues[0].id



    # Due Diligence
    * call read('classpath:features/portfolio/loans/loanDecisionSteps.feature@dueDiligenceStage') { dueDiligenceOn : '#(submittedOnDate)', loanId : '#(loanId)', surveyLocation : '#(surveyLocationValueId)', country : '#(countryValueId)', program : '#(programValueId)', cohort : '#(cohortValueId)' }
    #     Assert that Loan Account has passed DUE_DILIGENCE Stage
    * def loanResponseAfterDueDiligence = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert loanResponseAfterDueDiligence.loanAccount.loanDecisionState.id == 1200
    * assert loanResponseAfterDueDiligence.loanAccount.loanDecisionState.value == 'DUE_DILIGENCE'
    * assert loanResponseAfterDueDiligence.loanAccount.isExtendLoanLifeCycleConfig == true


    #- Disable configuration  ---Add-More-Stages-To-A-Loan-Life-Cycle---
    Then print 'Configuration ID ==> ', configurationId
    * def configResponse = call read('classpath:features/portfolio/configuration/configurationsteps.feature@disable_global_config') { configurationsId : '#(configurationId)' }
    Then print 'Configuration Response ==> ', configResponse
