package com.mattLearn.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Matt
 * @date 2021/1/18 18:51
 */
@Service
public class SensitiveWordService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordService.class);

    private static final String DEFAULT_REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    public void addWord(String word){
        TrieNode tempNode = rootNode;
        // 循环每个字节
        for(int i = 0; i < word.length(); ++i){
            Character c = word.charAt(i);
            // 过滤空格
            if(isSymbol(c)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if(node == null){ // 说明原前缀树中没有对应的结点
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            // 更新根结点
            tempNode = node;

            if(i == word.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder res = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0;  // 不回退的指针
        int position = 0;   // 实时比较指针，匹配失败则回退

        while(position < text.length()){
            Character c = text.charAt(position);
            // 是空格符号的话直接跳过
            // 用于捕获类似 “色|情” 这类中间插入特殊字符的非法词汇
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    res.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            // 情况1： 当前位置字符无匹配
            if(tempNode == null){
                res.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                tempNode = rootNode;    // 回到根结点
                // 情况2：找到相符的字符，并且是敏感词的结尾
            } else if(tempNode.isKeywordEnd()){
                // 发现敏感词
                res.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
                // 情况3: 找到相符的字符，但是不是敏感词的结尾
            }else{
                // 当前字符有匹配，但是还没有到敏感词的末尾
                ++position;
            }
        }
        res.append(text.substring(begin));
        return res.toString();
    }


    private boolean isSymbol(Character c){
        int intC = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (intC < 0x2E80 || intC > 0x9FFF);   // 0x2e80-0x9fff 东亚文字
    }


    // 该方法在初始化 bean 的 时候执行
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
           logger.error("Read the file failed." + e.getMessage());
        }
    }



    // 内部类，存放 trieNode 对象
    private class TrieNode{
        // 是不是关键词的结尾
        private boolean end = false;

        // 当前节点下的所有的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        void addSubNode(Character key, TrieNode node){
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        int getSubNodeCount(){
            return subNodes.size();
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }

    public static void main(String[] args) {
        SensitiveWordService s = new SensitiveWordService();
        s.addWord("haha");
        s.addWord("色情");
        System.out.println(s.filter("你好色情呵呵haha123"));
    }
}
