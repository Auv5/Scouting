# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

def get_match(number):
    return Match._matches[number]

class Match:
    _matches = {}
    def __init__(self, id, start_at, red, blue):
        self.red = red
        self.blue = blue
        self.id = id
        self.start_at = start_at
        self._matches[id] = self

    def to_json(self, t):
        return {'id': self.id, 'red': [r.number for r in self.red], 'blue':
            [b.number for b in self.blue], 'scout': t.number}
