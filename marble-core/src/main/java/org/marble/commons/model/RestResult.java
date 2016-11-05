package org.marble.commons.model;

public class RestResult {
        private String message;
        
        public RestResult() {
        }
        public RestResult(String message) {
            this.message = message;
        }
        public String getMessage(){
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
}
