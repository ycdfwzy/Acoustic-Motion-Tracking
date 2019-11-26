function [c,lags] = myxcorr(X, Y)
%MYXCORR 此处显示有关此函数的摘要
%   此处显示详细说明
    [~, n] = size(X);
    [~, m] = size(Y);
    if n > m
        Y = [Y, zeros(1, n-m)];
    else
        X = [X, zeros(1, m-n)];
    end
    
    N = max(n, m);
    
    Y = flip(Y);
    % convolution
    X = [X, zeros(1, N-1)];
    Y = [Y, zeros(1, N-1)];
    fft_X = fft(X);
    fft_Y = fft(Y);
    c = real(ifft(fft_X.*fft_Y));
    lags = 1-N:N-1;
end
