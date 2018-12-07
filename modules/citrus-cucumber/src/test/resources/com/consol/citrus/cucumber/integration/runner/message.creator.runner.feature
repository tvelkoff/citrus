Feature: Message creator runner features

Background:
  Given message creator com.consol.citrus.cucumber.integration.echo.EchoMessageCreator
  And variable operation is "sayHello"
  And variable text is "Hello"

  Scenario: Send and receive
    When <fooEndpoint> sends message <echoRequest>
    Then <fooEndpoint> should receive message <echoRequest>