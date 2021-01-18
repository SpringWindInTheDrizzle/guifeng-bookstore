/**
 * FileName: WebSocketConfig
 * Author:   sky
 * Date:     2020/5/8 21:24
 * Description:
 */
package com.jingshi.school.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *
 *
 * @author sky
 * @create 2020/5/8
 * @since 1.0.0
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}