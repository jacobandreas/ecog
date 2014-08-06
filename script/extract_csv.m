load('EC36_B4_HilbAA_70to150_8band_all_0_mel_out_resp.mat')

for i=1:size(out,2)
    record = out(i);
    name = ['csv/', record.name '.csv'];
    startsample = record.dataf * record.befaft(1);
    endsample = size(record.resp,2) - record.dataf * record.befaft(2) - 1;
    trimmedresp = record.resp(:,startsample:endsample,:);
    meanresp = mean(trimmedresp, 3);
    csvwrite(name, meanresp);
end