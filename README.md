# Zluetooth
Acoustic communication using FSK modulation tech, IOT course project, Jiliang Wang, Tsinghua. 2020

#### DistanceEstimation

- DistanceEstimation.java 使用**beepbeep**原理进行手机测距

#### FSK

- Decoder.java  包含录音接口和解码接口

  - locate_start 检测同步码位置的函数，使用了**滑动窗口**算法，检测chirp的位置，耦合MatchFilter.java
  - prepare_test_data 我们用来测试BFSK的函数
  - recover_data_packet 解码所有数据包的包长和payload
  - decode_packet_length 解码包长，8FSK 包长用9位表示，BFSK 用8位表示
  - recover_signal 装配解码器，恢复信号
  - demodule 解码

- Demodulator.java 解码

  - initFrequency 初始化频率
  - initCarriers 初始化载波
  - demodulate 解码，没有滤波过程，使用了匹配滤波，梯度积分

- Encoder.java 编码

  - splitPacket 将文本数据按字节划分为若干段
  - Encoder 数据包分包，对每一段的文本求其二进制，并得到二进制的码流

- MatchFilter.java 匹配信号，有检测信号强度，互相关，FFT等方法可以被用来使用做同步码检测

  - getSync 得到downchirp 同步码
  - matchSignalAmp 利用信号幅度做检测
  - matchSignalCorr 使用信号相关性做检测
  - matchSignal 使用FFT 做相关性检测

- Modulator.java 调制信号

  - modulate 在每一段data的前后，都加上10000个采样点以将数据包分开，这样比较好找同步码的位置每3个data，作为一个symbol去编码得到调制信号
  - toNumber 将2进制转为10进制

- SignalGenerator.java 信号产生

  - generate 产生正弦信号

  - generate_chirp_sync 产生chirp信号，公式如下

    $$\Large f(t) = A * (2  \pi (\frac{k}{2}*t*(\frac{1}{sr}) + sf)*i*\frac{1}{sr})$$

    $\large sf = start~frequency, sr = sample~rate, ef = end~frequency, k = \frac{ef-sf}{symbol~size}$

- DataPacket.java 数据包

- Permission 获取手机权限

- Recorder 录音，并写入.wav文件

- StringAndBinary 字符串和定长二进制之间的转换，此处使用UTF-8编码

- WavFile 音频文件写入

#### WiFiComm

- ApManager 得到本地ip
- Client 客户端
- Server 服务端

#### interface
![1](https://github.com/ZangJac/Zluetooth/blob/main/img/1.jpg)

![2](https://github.com/ZangJac/Zluetooth/blob/main/img/2.jpg)

