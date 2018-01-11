/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication;

/**
 *
 * @author rohith
 */
public class person {
    
    private String mobileno;
    private String username;
    private String password;
    private String question;
    private String answer;
    
    public void setMobile(String mobileno){
        this.mobileno = mobileno;
    }
    
    public String getMobile(){
        return mobileno;
    }
    
    public void setUserName(String username){
        this.username = username;
    }
    
    public String getUserName(){
        return username;
    }
    
    public void setPassword(String password) throws Exception{
        this.password = Aes.decrypt(password);
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setQuestion(String question){
        this.question = question;
    }
    
    public String getQuestion(){
        return question;
    }
    
    public void setAnswer(String answer) throws Exception{
        this.answer = Aes.decrypt(answer);
    }
    
    public String getAnswer(){
        return answer;
    }
}
