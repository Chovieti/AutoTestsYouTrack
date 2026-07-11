package org.example.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$x;

public class CommentSection {
  @FindBy(xpath = "//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']")
  private SelenideElement commentInput;
  @FindBy(xpath = "//div[@data-test='editor-actions']//button[@data-test='post-comment']")
  private SelenideElement commentPostButton;
  @FindBy(xpath = "//div[@data-test='ring-popup']//button[contains(@id, ':remove')]")
  private SelenideElement commentRemove;

  public CommentSection() {
    Selenide.page(this);
  }

  public CommentSection addComment(String text) {
    commentInput.setValue(text);
    commentPostButton.click();
    return this;
  }

  public CommentSection openMoreMenuForComment(String commentText) {
    String xpath = String.format(
        "//div[@data-test='change-item'][.//div[@data-test='comment-content']"
            + "//*[contains(text(),'%s')]]//button[@data-test='comment-menu']",
        commentText
    );
    SelenideElement moreButton = $x(xpath);
    moreButton.click();
    return this;
  }

  public void checkCommentExists(String commentText) {
    String xpath = String.format(
        "//div[@data-test='change-item'][.//div[@data-test='comment-content']"
            + "//*[contains(text(),'%s')]]",
        commentText
    );
    SelenideElement comment = $x(xpath).shouldBe(Condition.visible);
  }

  public void deleteComment(String commentText) {
    openMoreMenuForComment(commentText);
    commentRemove.click();
  }
}
