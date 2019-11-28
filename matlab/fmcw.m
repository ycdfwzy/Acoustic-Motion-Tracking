%% �����ź�����
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

%% �����źŶ�ȡ�����˲�
[mydata,fs] = audioread('chirp.m4a');
mydata = mydata(:,1);

% figure;
% plot(mydata);

hd = design(fdesign.bandpass('N,F3dB1,F3dB2',8,17000,21500,fs),'butter');
mydata=filter(hd,mydata);

% figure;
% plot(mydata);

 
%% ����pseudo-transmitted�ź�
pseudo_T = [];
for i = 1:88
    pseudo_T = [pseudo_T,data,zeros(1,T*fs+1)];
end

[n,~]=size(mydata);

% fmcw�źŵ���ʼλ����start��
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

len = (T*fs+1)*2; % chirp�źż����հ׵ĳ���֮��
fftlen = 1024*64; %�����ٸ���Ҷ�任ʱ����ĳ��ȡ������ݺ������ʹ�Ĳ��������࣬Ƶ�ʷֱ�����ߡ��������г��Բ�ͬ�Ĳ��㳤�ȶ��ڼ�������Ӱ�졣
f = fs*(0:fftlen -1)/(fftlen); %% ���ٸ���Ҷ�任����֮��õ���Ƶ�ʲ�����

%% ����ÿ��chirp�ź�����Ӧ��Ƶ��ƫ��
R = zeros(1, 88);
for i = start:len:start+len*87
   FFT_out = abs(fft(s(i:i+len/2),fftlen));
   [~, max_arg] = max(abs(FFT_out));
   R((i-start)/len+1) = max_arg * 340 * T / (f1-f0);
end

%% �����Ƶ��ƫ��delta f���������ı仯����
delta_distance = R;

%% ��������ı仯
figure;
plot((0:87)*2*T, delta_distance);
