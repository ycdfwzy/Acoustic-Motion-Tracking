%% 发送信号生成
fs = 44100;
T = 0.04;
f0 = 13500; % start freq
f1 = 16000;  % end freq
t = 0:1/fs:T ;
data = chirp(t, f0, T, f1, 'linear');

output = [];
for i = 1:88
    output = [output,data,zeros(1,fs*T+1)];
end

audiowrite('low.wav', [output, output, output], fs);

%% 接收信号读取，并滤波
[mydata,fs] = audioread('chirp.m4a');
mydata = mydata(:,1);

% figure;
% plot(mydata);

hd = design(fdesign.bandpass('N,F3dB1,F3dB2',8,17000,21500,fs),'butter');
mydata=filter(hd,mydata);

% figure;
% plot(mydata);

 
%% 生成pseudo-transmitted信号
pseudo_T = [];
for i = 1:88
    pseudo_T = [pseudo_T,data,zeros(1,T*fs+1)];
end

[n,~]=size(mydata);

% fmcw信号的起始位置在start处
[c, lags] = myxcorr(mydata', data);
max_c = max(c);
for i = 1:length(c)
    if c(i) >= max_c/2
        start =lags(i);
        break;
    end
end
pseudo_T = [zeros(1,start),pseudo_T];
[~,m]=size(pseudo_T);
pseudo_T = [pseudo_T,zeros(1,n-m)];

s=pseudo_T.*mydata';

% disp(sum(s(start+1:start+fs*T))/sqrt(sum(pseudo_T(start+1:start+fs*T).^2))/sqrt(sum(mydata.^2)))

len = (T*fs+1)*2; % chirp信号及其后空白的长度之和
fftlen = 1024*64; %做快速傅立叶变换时补零的长度。在数据后补零可以使的采样点增多，频率分辨率提高。可以自行尝试不同的补零长度对于计算结果的影响。
f = fs*(0:fftlen -1)/(fftlen); %% 快速傅立叶变换补零之后得到的频率采样点

%% 计算每个chirp信号所对应的频率偏移
R = zeros(1, 88);
for i = start:len:start+len*87
   FFT_out = abs(fft(s(i:i+len/2),fftlen));
   [~, max_arg] = max(abs(FFT_out));
   R((i-start)/len+1) = max_arg * 340 * T / (f1-f0);
end

%% 请根据频率偏移delta f计算出距离的变化量。
delta_distance = R;

%% 画出距离的变化
figure;
plot((0:87)*2*T, delta_distance);
