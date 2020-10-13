//
// Created by Constantin on 2/6/2019.
//

#ifndef LIVE_VIDEO_10MS_ANDROID_PARSERTP_H
#define LIVE_VIDEO_10MS_ANDROID_PARSERTP_H

#include <cstdio>
#include "../NALU/NALU.hpp"

/*********************************************
 ** Parses a stream of rtp h264 data into NALUs
**********************************************/

class ParseRTP{
public:
    ParseRTP(NALU_DATA_CALLBACK cb);
public:
    void parseData(const uint8_t* data,const size_t data_length);
    void reset();
    //Encoding
    int h264nal2rtp_send(int framerate,const uint8_t *pstStream, int nalu_len);
    void send_data_to_client_list(uint8_t *send_buf, size_t len_sendbuf);

private:
    const NALU_DATA_CALLBACK cb;
    std::array<uint8_t,NALU::NALU_MAXLEN> nalu_data;
    size_t nalu_data_length=0;
    //
    static constexpr std::size_t SEND_BUF_SIZE=1500;
    static constexpr std::size_t RTP_PAYLOAD_MAX_SIZE=1024*1024;
    std::array<uint8_t,SEND_BUF_SIZE> SENDBUFFER[SEND_BUF_SIZE];
};
#endif //LIVE_VIDEO_10MS_ANDROID_PARSERTP_H
