Feature:  Create Client Business Details APIs
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @createClientBusinessDetails
  Scenario: Create Client Business Details
    * def submittedOnDate = df.format(faker.date().past(370, 369, TimeUnit.DAYS))

    * def clientDetailsId = call read('classpath:features/portfolio/clientdetails/clientdetailssteps.feature@createClientBusinessDetailsStep'){clientCreationDate : '#(submittedOnDate)' }

