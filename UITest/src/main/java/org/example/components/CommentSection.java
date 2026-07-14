package org.example.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$x;

public class CommentSection {
  @FindBy(xpath = "//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']")
  private SelenideElement commentInput;// = $x("//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']");
  @FindBy(xpath = "//div[@data-test='editor-actions']//button[@data-test='post-comment']")
  private SelenideElement commentPostButton;// = $x("//div[@data-test='editor-actions']//button[@data-test='post-comment']");
  @FindBy(xpath = "//div[@data-test='ring-popup']//button[contains(@id, ':remove')]")
  private SelenideElement commentRemove;// = $x("//div[@data-test='ring-popup']//button[contains(@id, ':remove')]");

  public CommentSection() {
    Selenide.page(this);
  }

  public CommentSection addComment(String text) {
    commentInput.setValue(text);
    commentPostButton.click();
    String xpathOfNewComment = String.format(
        "//div[@data-test='change-item'][.//div[@data-test='comment-content']//*[contains(text(),'%s')]]",
        text
    );
    $x(xpathOfNewComment).shouldBe(Condition.visible);
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

  public boolean checkCommentExists(String commentText) {
    String xpath = String.format(
        "//div[@data-test='change-item'][.//div[@data-test='comment-content']"
            + "//*[contains(text(),'%s')]]",
        commentText
    );
    try {
      $x(xpath).shouldBe(Condition.visible);
    } catch (AssertionError e) {
      return false;
    }
    return true;
  }

  public void deleteComment(String commentText) {
    openMoreMenuForComment(commentText);
    commentRemove.click();
  }
}
