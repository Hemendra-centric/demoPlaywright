package llm.parser;

import io.cucumber.java.en.*;

public class api_REQ_203_Fund_Transfer_APISteps {

    @Given("I send request: {fromAccount=string, toAccount=string, amount=1000}")
    public void i_send_request_fromaccountstring_toaccountstring_amount1000() {
        // TODO: implement step
    }

    @Then("I verify response: {statusCode=200}")
    public void i_verify_response_statuscode200() {
        // TODO: implement step
    }

    @Given("I send request: {fromAccount=string, toAccount=string, amount=50000, otp=string}")
    public void i_send_request_fromaccountstring_toaccountstring_amount50000_otpstring() {
        // TODO: implement step
    }

    @Then("I verify response: {statusCode=200, bodyContains=[transactionId]}")
    public void i_verify_response_statuscode200_bodycontainstransactionid() {
        // TODO: implement step
    }

}
