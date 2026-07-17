package org.example.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.*;

public class LoginPage extends BasePage<LoginPage> {
  @FindBy(xpath = "//button[@data-test='ring-link login-button']")
  private SelenideElement loginButton;
  @FindBy(xpath = "//iframe[@title='Login dialog']")
  private SelenideElement loginFrame;
  @FindBy(xpath = "//input[@data-test='username-field']")
  private SelenideElement usernameInput;
  @FindBy(xpath = "//input[@data-test='password-field']")
  private SelenideElement passwordInput;
  @FindBy(xpath = "//button[@data-test='login-button']")
  private SelenideElement submitButton;

  @Override
  public LoginPage waitForPageLoaded() {
    loginButton.shouldBe(Condition.visible);
    return this;
  }

  public LoginPage openPage(String baseUrl) {
    open(baseUrl);
    return this;
  }

  public MainPage login(String username, String password) {
    loginButton.click();
    switchTo().frame(loginFrame);
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
    switchTo().defaultContent();
    loginButton.shouldNotBe(Condition.visible);
    return new MainPage();
  }
}
