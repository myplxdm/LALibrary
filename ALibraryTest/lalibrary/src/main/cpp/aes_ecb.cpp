#include <assert.h>
#include "aes.h"
#include "aes_locl.h"
#include <ctype.h>


void bin2Hex(char *bin, size_t size, char *asc, size_t asc_size)
{
    memset(asc, 0, asc_size);
    static char tab[] = "0123456789ABCDEF";
    unsigned char *src = (unsigned char *)bin;
    unsigned char *dst = (unsigned char *)asc;
    while (size-- > 0)
    {
        *dst++ = tab[*src >> 4];
        *dst++ = tab[*src & 0xF];
        ++src;
    }
}

int hex2bin(const char *pSrc, unsigned char *pDst, int nSrcLength)
{
    short i;
    unsigned char highByte, lowByte;

    for (i = 0; i < nSrcLength; i += 2)
    {
        highByte = toupper(pSrc[i]);
        lowByte  = toupper(pSrc[i + 1]);

        if (highByte > 0x39)
            highByte -= 0x37;
        else
            highByte -= 0x30;

        if (lowByte > 0x39)
            lowByte -= 0x37;
        else
            lowByte -= 0x30;

        pDst[i / 2] = (highByte << 4) | lowByte;
    }
    return nSrcLength / 2;
//    int nDstLength = 0;
//    for ( int j = 0; pSrc[j]; j++ )
//    {
//        if (isxdigit(pSrc[j]))
//            nDstLength++;
//    }
//    if ( nDstLength & 0x01 ) nDstLength++;
//    nDstLength /= 2;
//
//    if ( nDstLength > nSrcLength )
//        return -1;
//
//    nDstLength = 0;
//    int phase = 0;
//    for ( int i = 0; pSrc[i]; i++ )
//    {
//        if (!isxdigit(pSrc[i]))
//            continue;
//
//        unsigned char val = pSrc[i] - (isdigit(pSrc[i]) ? 0x30 : ( isupper(pSrc[i]) ? 0x37 : 0x57 ) );
//
//        if ( phase == 0 )
//        {
//            pDst[nDstLength] = val << 4;
//            phase++;
//        }
//        else
//        {
//            pDst[nDstLength] |= val;
//            phase = 0;
//            nDstLength++;
//        }
//    }
//    return nDstLength;
}

void AES_ecb_encrypt(const unsigned char *in, unsigned char *out,
		const AES_KEY *key, const int enc) {

	assert(in && out && key);
	assert((AES_ENCRYPT == enc)||(AES_DECRYPT == enc));

	if (AES_ENCRYPT == enc)
		AES_encrypt(in, out, key);
	else
		AES_decrypt(in, out, key);
}

int aes_ecb_encrypt_PKCS5Padding(char * in, int len, char * out, char * key,
		int keyBit)
{
	AES_KEY aes;
	if (!in || !key || !out
		|| AES_set_encrypt_key((unsigned char*) key, 128, &aes) < 0)
		return 0;
	int en_len = 0;
	int y = AES_BLOCK_SIZE - (len % AES_BLOCK_SIZE);
    
	char buffer[len + y];
    memset(buffer, 0, len + y);
	memcpy(buffer, in, len);
    memset(&buffer[len], y, y);
    len += y;
	while (en_len < len)
	{
		AES_encrypt((unsigned char*)&buffer[en_len], (unsigned char*)out, &aes);
		out += AES_BLOCK_SIZE;
		en_len += AES_BLOCK_SIZE;
	}
	return len;
}

int aes_ecb_decrypt_PKCS5Padding(char* in, int len, char* out, char* key,int keyBit)
{
	AES_KEY aes;
	if (!in || !key || !out
			|| AES_set_decrypt_key((unsigned char*) key, 128, &aes) < 0)
		return 0;
	int en_len = 0;
	while (en_len < len) {
		AES_decrypt((unsigned char*) in, (unsigned char*) out, &aes);
		in += AES_BLOCK_SIZE;
		out += AES_BLOCK_SIZE;
		en_len += AES_BLOCK_SIZE;
	}
	return en_len;
}
