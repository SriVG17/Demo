Feature: IRCTC Train Search Automation

  Scenario: Search trains from Hyderabad to Pune
    Given I launch the IRCTC application
    When I handle the initial dialog if present
    And I enter "Hyd" as the From station
    And I select "HYB" from the suggestions
    And I enter "Pune" as the To station
    And I select "PUNE JN - PUNE" from the suggestions
    And I select journey date as 4 days from today
    And I choose "Sleeper (SL)" class
    And I check the Person With Disability Concession option
    And I confirm the PWD popup
    And I click on search trains
    Then I should see the list of available trains
    And I capture a full page screenshot of the results
